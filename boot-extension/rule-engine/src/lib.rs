#![feature(lazy_cell)]

use std::sync::LazyLock;

use jni::JNIEnv;
use jni::objects::{JByteArray, JClass};
use jni::sys::{jboolean, jint};
use serde_json::{Error, error, json};
use zen_engine::{DecisionEngine, EvaluationOptions};
use zen_engine::loader::NoopLoader;
use zen_engine::model::DecisionContent;

static ENGINE_LAZY: LazyLock<DecisionEngine<NoopLoader>> = LazyLock::new(|| {
    println!("initializing");
    DecisionEngine::default()
});

/*
 * Class:     com_workoss_boot_engine_ZenEngineLoader
 * Method:    evaluate
 * Signature: ([B[BZI)[B
 */
#[tokio::main]
#[no_mangle]
pub async extern "system" fn Java_com_workoss_boot_engine_ZenEngineLoader_evaluate<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    decision: JByteArray<'local>,
    input: JByteArray<'local>,
    trace: jboolean,
    max_depth: jint,
) -> JByteArray<'local> {
    let decision = env.convert_byte_array(&decision).unwrap();
    let input = env.convert_byte_array(&input).unwrap();
    let decision_result: error::Result<DecisionContent> = serde_json::from_slice(decision.as_slice());
    if decision_result.is_err() {
        env.throw_new("com/workoss/boot/engine/RuleEngineException",
                      decision_result.err().unwrap().to_string())
            .unwrap();
        return JByteArray::default();
    }
    let decision_content: DecisionContent = serde_json::from_slice(decision.as_slice()).unwrap();
    let options: EvaluationOptions = EvaluationOptions {
        trace: Some(trace.eq(&1)),
        max_depth: Some(max_depth as u8),
    };
    let decision = ENGINE_LAZY.create_decision(decision_content.into());
    let result = decision.evaluate_with_opts(&json!(input), options).await;
    if result.is_err() {
        env.throw_new("com/workoss/boot/engine/RuleEngineException",
                      result.err().unwrap().to_string())
            .unwrap();
        return JByteArray::default();
    }
    let response = result.unwrap();
    env.byte_array_from_slice(serde_json::to_vec(&response).unwrap().as_slice()).unwrap()
}

/*
 * Class:     com_workoss_boot_engine_ZenEngineLoader
 * Method:    validate
 * Signature: ([B)Z
 */
#[no_mangle]
pub extern "system" fn Java_com_workoss_boot_engine_ZenEngineLoader_validate<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    decision: JByteArray<'local>,
) -> jboolean {
    let decision = env.convert_byte_array(&decision).unwrap();
    let result: Result<DecisionContent, Error> = serde_json::from_slice(decision.as_slice());
    if result.is_err() {
        env.throw_new("com/workoss/boot/engine/RuleEngineException",
                      result.err().unwrap().to_string())
            .unwrap();
        return 0;
    }
    let valid_result = ENGINE_LAZY.create_decision(result.unwrap().into()).validate();
    if valid_result.is_err() {
        env.throw_new("com/workoss/boot/engine/RuleEngineException",
                      valid_result.err().unwrap().to_string())
            .unwrap();
        return 0;
    }
    return 1;
}

use std::sync::OnceLock;

use jni::objects::{JByteArray, JClass};
use jni::sys::{jboolean, jint};
use jni::JNIEnv;
use serde_json::{error, Error, Value};
use tokio::task::JoinError;
use zen_engine::loader::NoopLoader;
use zen_engine::model::DecisionContent;
use zen_engine::{DecisionEngine, EvaluationOptions};
use zen_expression::{Isolate, IsolateError};

static RULE_ENGINE_LAZY: OnceLock<DecisionEngine<NoopLoader>> = OnceLock::new();

/*
 * 初始化懒加载获取 决策引擎
 */
fn get_rule_engine() -> &'static DecisionEngine<NoopLoader> {
    RULE_ENGINE_LAZY.get_or_init(|| {
        println!("initializing rule engine");
        DecisionEngine::default()
    })
}

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
    let decision = env.convert_byte_array(decision).unwrap();
    let input = env.convert_byte_array(input).unwrap();
    let decision_result: error::Result<DecisionContent> =
        serde_json::from_slice(decision.as_slice());
    if decision_result.is_err() {
        env.throw_new(
            "com/workoss/boot/engine/RuleEngineException",
            decision_result.err().unwrap().to_string(),
        )
        .unwrap();
        return JByteArray::default();
    }

    let job_handler = tokio::spawn(async move {
        let decision_engine = get_rule_engine().create_decision(decision_result.unwrap().into());
        let input_content: Value = serde_json::from_slice(input.as_slice()).unwrap();
        futures::executor::block_on(decision_engine.evaluate_with_opts(
            &input_content,
            EvaluationOptions {
                trace: Some(trace.eq(&1)),
                max_depth: Some(max_depth as u8),
            },
        ))
    })
    .await;
    return match job_handler {
        Ok(response_result) => match response_result {
            Ok(respone) => env
                .byte_array_from_slice(serde_json::to_vec(&respone).unwrap().as_slice())
                .unwrap(),
            Err(error) => {
                env.throw_new(
                    "com/workoss/boot/engine/RuleEngineException",
                    error.to_string(),
                )
                .unwrap();
                JByteArray::default()
            }
        },
        Err(_) => {
            env.throw_new(
                "com/workoss/boot/engine/RuleEngineException",
                "hook time out",
            )
            .unwrap();
            JByteArray::default()
        }
    };
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
    let decision_result: Result<DecisionContent, Error> =
        serde_json::from_slice(decision.as_slice());
    if decision_result.is_err() {
        env.throw_new(
            "com/workoss/boot/engine/RuleEngineException",
            decision_result.err().unwrap().to_string(),
        )
        .unwrap();
        return 0;
    }
    return match get_rule_engine()
        .create_decision(decision_result.unwrap().into())
        .validate()
    {
        Ok(_) => 1,
        Err(error) => {
            env.throw_new(
                "com/workoss/boot/engine/RuleEngineException",
                error.to_string(),
            )
            .unwrap();
            return 0;
        }
    };
}

#[tokio::main]
#[no_mangle]
pub async extern "system" fn Java_com_workoss_boot_engine_ZenEngineLoader_expression<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    expression: JByteArray<'local>,
    input: JByteArray<'local>,
) -> JByteArray<'local> {
    let expression = env.convert_byte_array(expression).unwrap();
    let input = env.convert_byte_array(input).unwrap();
    let expression_content = String::from_utf8(expression).unwrap();
    let input_content: Value = serde_json::from_slice(input.as_slice()).unwrap();

    let join_handle: Result<Result<Value, IsolateError>, JoinError> = tokio::spawn(async move {
        return Isolate::with_environment(&input_content).run_standard(&expression_content);
    })
    .await;

    return match join_handle {
        Ok(value_result) => match value_result {
            Ok(response) => env
                .byte_array_from_slice(serde_json::to_vec(&response).unwrap().as_slice())
                .unwrap(),
            Err(error) => {
                env.throw_new(
                    "com/workoss/boot/engine/RuleEngineException",
                    error.to_string(),
                )
                .unwrap();
                JByteArray::default()
            }
        },
        Err(_) => {
            env.throw_new(
                "com/workoss/boot/engine/RuleEngineException",
                "hook time out",
            )
            .unwrap();
            JByteArray::default()
        }
    };
}

#[cfg(test)]
mod tests {
    use serde_json::{json, Value};
    use zen_expression::Isolate;

    #[test]
    fn it_works() {
        let mut isolate = Isolate::new();
        let json_env: Value = json!({"a":1,"b":2,"c":3});
        isolate.set_environment(&json_env);
        let expr = r#"a+b+c"#;
        let value = isolate.run_standard(expr).unwrap();
        let result = serde_json::to_string(&value).unwrap();
        assert_eq!("6", result);
        assert_eq!(json!(6), value);
    }
}

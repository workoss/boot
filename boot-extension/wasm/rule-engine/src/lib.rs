cargo_component_bindings::generate!({});

use bindings::Guest;
use serde::Deserialize;
use serde_json::Value;
use std::sync::OnceLock;
use zen_engine::loader::NoopLoader;
use zen_engine::model::DecisionContent;
use zen_engine::{DecisionEngine, EvaluationOptions};
use zen_expression::isolate::Isolate;

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

struct Component;

#[derive(Clone, Debug, PartialEq, Deserialize)]
enum CallFunc {
    EXPRESSION,
    VALIDATE,
    RULE,
}

#[derive(Clone, Debug, PartialEq, Deserialize)]
struct ExpressionContext {
    call_func: CallFunc,
    expression: Option<String>,
    decision: Option<String>,
    trace: Option<bool>,
    max_depth: Option<u8>,
}

#[warn(unreachable_code)]
impl Guest for Component {
    fn run(input: Vec<u8>, context: Option<Vec<u8>>) -> Result<Vec<u8>, String> {
        if context.is_none() {
            return Err(String::from("rule engine context need call_func"));
        }
        let context: ExpressionContext =
            serde_json::from_slice(&context.unwrap()).expect("context is null");

        // bindings::workoss::plugin::logger::log("");

        let result = match context.call_func {
            CallFunc::EXPRESSION => {
                return match context.expression {
                    Some(expre) => run_expression(expre, &input),
                    None => Err(format!(
                        "run expression error ,expression should not be null"
                    )),
                };
            }
            CallFunc::VALIDATE => {
                return match context.decision {
                    Some(decision_content) => validate(decision_content),
                    None => Err(format!(
                        "run gorule validate error ,decision should not be null"
                    )),
                };
            }
            CallFunc::RULE => {
                return match context.decision {
                    Some(decision_content) => {
                        let trace = match context.trace {
                            Some(s) => s,
                            None => false,
                        };
                        let max_depth = match context.max_depth {
                            Some(s) => s,
                            None => 5,
                        };
                        execute(decision_content, &input, trace, max_depth)
                    }
                    None => Err(format!(
                        "run gorule execute error ,decision should not be null"
                    )),
                };
            }
        };
        result
    }
}

fn execute(
    decision: String,
    input: &Vec<u8>,
    trace: bool,
    max_depth: u8,
) -> Result<Vec<u8>, String> {
    let decision_result: serde_json::error::Result<DecisionContent> =
        serde_json::from_str(&decision);

    if decision_result.is_err() {
        return Err(format!("run gorule execute error "));
    }
    let decision_engine = get_rule_engine().create_decision(decision_result.unwrap().into());
    let input_content: Value = serde_json::from_slice(input).unwrap();

    let result = futures::executor::block_on(decision_engine.evaluate_with_opts(
        &input_content,
        EvaluationOptions {
            trace: Some(trace),
            max_depth: Some(max_depth),
        },
    ));

    return match result {
        Ok(response) => Ok(serde_json::to_vec(&response).unwrap()),
        Err(error) => Err(format!("run gorule execute error:{}", error.to_string())),
    };
}

fn validate(decision: String) -> Result<Vec<u8>, String> {
    let decision_result: serde_json::error::Result<DecisionContent> =
        serde_json::from_str(&decision);

    if decision_result.is_err() {
        return Ok(vec![0]);
    }

    return match get_rule_engine()
        .create_decision(decision_result.unwrap().into())
        .validate()
    {
        Ok(_) => Ok(vec![1]),
        Err(_) => Ok(vec![0]),
    };
}

fn run_expression(expression: String, input: &Vec<u8>) -> Result<Vec<u8>, String> {
    let input_content: Value = serde_json::from_slice(input).unwrap();
    let iso = Isolate::default();
    iso.inject_env(&input_content);
    return match iso.run_standard(&expression) {
        Ok(response) => Ok(serde_json::to_vec(&response).unwrap()),
        Err(error) => Err(format!("run expression error {}", error)),
    };
}

#[cfg(test)]
mod test {
    use super::*;

    #[test]
    fn test_run() {
        let json = r#"
        {
            "name": "张三",
            "call_func": "EXPRESSION",
            "expression1":"a * b",
            "pet_phrase": [
                "Bond, James Bond.",
                "Shaken, not stirred."
            ]
        }"#;
        let context: ExpressionContext =
            serde_json::from_slice(&json.as_bytes()).expect("context is null");
        println!("{:#?}", context);
    }
}

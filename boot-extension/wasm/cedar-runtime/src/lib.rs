cargo_component_bindings::generate!({});

use bindings::Guest;
use serde::Deserialize;

struct Component;

#[derive(Clone, Debug, PartialEq, Deserialize)]
enum CallFunc {
    AuthorizationOperation,
    ValidateOperation,
}

#[derive(Clone, Debug, PartialEq, Deserialize)]
struct CedarContext {
    call_func: CallFunc,
}

impl Guest for Component {
    fn run(input: Vec<u8>, context: Option<Vec<u8>>) -> Result<Vec<u8>, String> {
        if context.is_none() {
            return Err(String::from("cedar context need call_func"));
        }
        let context: CedarContext =
            serde_json::from_slice(&context.unwrap()).expect("cedar context is null");
        // let runtime = tokio::runtime::Builder::new_current_thread()
        //     .enable_all()
        //     .build()
        //     .unwrap();
        // let result = runtime.block_on(async move { call_cedar(context.call_func, input) });

        let result = call_cedar(context.call_func, input);
        // let handle = async_std::spawn(move || call_cedar(context.call_func, input));
        result
    }
}

fn call_cedar(call_fun: CallFunc, input: Vec<u8>) -> Result<Vec<u8>, String> {
    let input_json_result = std::str::from_utf8(&input);
    if input_json_result.is_err() {
        return Err(format!("cedar input json error"));
    }
    let input_json = input_json_result.unwrap();
    // bindings::workoss::plugin::logger::log("");
    let result = match call_fun {
        CallFunc::AuthorizationOperation => {
            cedar_policy::frontend::is_authorized::json_is_authorized(input_json)
        }
        CallFunc::ValidateOperation => cedar_policy::frontend::validate::json_validate(input_json),
    };
    let resp = serde_json::to_vec(&result).expect("could not serialise response");
    Ok(resp)
}

#[cfg(test)]
mod tests {
    use cedar_policy::frontend::utils::InterfaceResult;

    use super::*;

    #[test]
    fn empty_authorization_call_succeeds() {
        let input = r#"
        {
            "principal": "User::\"alice\"",
            "action": "Photo::\"view\"",
            "resource": "Photo::\"photo\"",
            "slice": {
              "policies": {},
              "entities": []
            },
            "context": {}
          }
        "#;
        let result =
            call_cedar(CallFunc::AuthorizationOperation, input.as_bytes().to_vec()).unwrap();
        let resp = String::from_utf8(result).unwrap();
        println!("resp: {}", &resp);
        assert_success(resp);
    }

    #[test]
    fn empty_validation_call_succeeds() {
        let input = r#"{ "schema": { "": {"entityTypes": {}, "actions": {} } }, "policySet": {} }"#;
        let result = call_cedar(CallFunc::ValidateOperation, input.as_bytes().to_vec()).unwrap();
        let resp = String::from_utf8(result).unwrap();
        println!("resp: {}", &resp);
        assert_success(resp);
    }

    #[test]
    fn test_unspecified_principal_call_succeeds() {
        let input = r#"
        {
            "context": {},
            "slice": {
              "policies": {
                "001": "permit(principal, action, resource);"
              },
              "entities": [],
              "templates": {},
              "template_instantiations": []
            },
            "principal": null,
            "action": "Action::\"view\"",
            "resource": "Resource::\"thing\""
        }
        "#;
        let result =
            call_cedar(CallFunc::AuthorizationOperation, input.as_bytes().to_vec()).unwrap();
        let resp = String::from_utf8(result).unwrap();
        println!("resp: {}", &resp);
        assert_success(resp);
    }

    #[test]
    fn test_unspecified_resource_call_succeeds() {
        let input = r#"
        {
            "context": {},
            "slice": {
              "policies": {
                "001": "permit(principal, action, resource);"
              },
              "entities": [],
              "templates": {},
              "template_instantiations": []
            },
            "principal": "User::\"alice\"",
            "action": "Action::\"view\"",
            "resource": null
        }
        "#;

        let result =
            call_cedar(CallFunc::AuthorizationOperation, input.as_bytes().to_vec()).unwrap();
        let resp = String::from_utf8(result).unwrap();
        println!("resp: {}", &resp);
        assert_success(resp);
    }

    #[test]
    fn template_authorization_call_succeeds() {
        let input = r#"
        { "principal": "User::\"alice\""
        , "action" : "Photo::\"view\""
        , "resource" : "Photo::\"door\""
        , "context" : {}
        , "slice" : {
              "policies" : {}
            , "entities" : []
            , "templates" : {
               "ID0": "permit(principal == ?principal, action, resource);"
             }
            , "template_instantiations" : [
               {
                   "template_id" : "ID0",
                   "result_policy_id" : "ID0_User_alice",
                   "instantiations" : [
                       {
                           "slot": "?principal",
                           "value": {
                               "ty" : "User",
                               "eid" : "alice"
                           }
                       }
                   ]
               }
            ]
        }
     }
    "#;
        let result =
            call_cedar(CallFunc::AuthorizationOperation, input.as_bytes().to_vec()).unwrap();
        let resp = String::from_utf8(result).unwrap();
        println!("resp: {}", &resp);
        assert_success(resp);
    }

    fn assert_success(result: String) {
        let result: InterfaceResult = serde_json::from_str(result.as_str()).unwrap();
        match result {
            InterfaceResult::Success { .. } => {}
            InterfaceResult::Failure { .. } => panic!("expected a success, not {:?}", result),
        };
    }
}

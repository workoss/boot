use std::sync::OnceLock;

use jni::objects::{JByteArray, JClass};
use jni::JNIEnv;
use wasmtime::component::{Component, Linker};
use wasmtime::{Engine, Store};
use wasmtime_wasi::preview2::{DirPerms, FilePerms, Table, WasiCtx, WasiCtxBuilder, WasiView};
use wasmtime_wasi::{ambient_authority, Dir};

wasmtime::component::bindgen!({
    path: "./wit/plugin.wit",
    world: "plugin",
    async: true
});

//GLOBAL_ENGINE
static GLOBAL_ENGINE: OnceLock<Engine> = OnceLock::new();

fn get_wasmtime_engine() -> &'static Engine {
    GLOBAL_ENGINE.get_or_init(|| {
        let mut engine_config = wasmtime::Config::new();
        engine_config.wasm_component_model(true);
        engine_config.async_support(true);
        engine_config.wasm_backtrace_details(wasmtime::WasmBacktraceDetails::Disable);
        engine_config.debug_info(true);
        Engine::new(&engine_config).unwrap()
    })
}

struct PluginLogger;

struct PluginCtx {
    logger: PluginLogger,
    table: Table,
    context: WasiCtx,
}

impl WasiView for PluginCtx {
    fn table(&self) -> &Table {
        &self.table
    }

    fn table_mut(&mut self) -> &mut Table {
        &mut self.table
    }

    fn ctx(&self) -> &WasiCtx {
        &self.context
    }

    fn ctx_mut(&mut self) -> &mut WasiCtx {
        &mut self.context
    }
}

#[async_trait::async_trait]
impl workoss::plugin::logger::Host for PluginLogger {
    async fn log(&mut self, msg: String) -> wasmtime::Result<String> {
        println!("{}", msg);
        Ok(String::from("ok"))
    }
}

#[tokio::main]
#[no_mangle]
pub async extern "system" fn Java_com_workoss_boot_wasm_WasmPluginLoader_run<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    plugin_file: JByteArray<'local>,
    input: JByteArray<'local>,
    context: JByteArray<'local>,
) -> JByteArray<'local> {
    let plugin_bytes = match env.convert_byte_array(plugin_file) {
        Ok(plugin) => plugin,
        Err(_) => {
            env.throw_new(
                "com/workoss/boot/wasm/WasmPluginException",
                "load plugin error",
            )
            .unwrap();
            vec![]
        }
    };
    let input = match env.convert_byte_array(input) {
        Ok(input_content) => input_content,
        Err(error) => {
            env.throw_new(
                "com/workoss/boot/wasm/WasmPluginException",
                error.to_string(),
            )
            .unwrap();
            vec![]
        }
    };

    let context = match env.convert_byte_array(context) {
        Ok(ctx) => ctx,
        Err(error) => {
            env.throw_new(
                "com/workoss/boot/wasm/WasmPluginException",
                error.to_string(),
            )
            .unwrap();
            vec![]
        }
    };

    let result = call_wasm(None, Some(&plugin_bytes), &input, Some(&context));
    return match result.await {
        Ok(output) => env.byte_array_from_slice(output.as_slice()).unwrap(),
        Err(error) => {
            env.throw_new("com/workoss/boot/wasm/WasmPluginException", error)
                .unwrap();
            JByteArray::default()
        }
    };
}

async fn call_wasm(
    plugin_file: Option<&str>,
    plugin_bytes: Option<&[u8]>,
    input: &[u8],
    context: Option<&[u8]>,
) -> Result<Vec<u8>, String> {
    let engine = get_wasmtime_engine();

    let mut linker: Linker<PluginCtx> = Linker::new(&engine);

    wasmtime_wasi::preview2::command::add_to_linker(&mut linker).unwrap();

    Plugin::add_to_linker(&mut linker, |state: &mut PluginCtx| &mut state.logger).unwrap();

    let table = wasmtime_wasi::preview2::Table::new();

    let wasi_ctx = WasiCtxBuilder::new()
        .inherit_stdin()
        .inherit_stdout()
        .inherit_stderr()
        .preopened_dir(
            Dir::open_ambient_dir(std::env::current_dir().unwrap(), ambient_authority()).unwrap(),
            DirPerms::all(),
            FilePerms::all(),
            ".",
        )
        .build();

    let mut store = Store::new(
        &engine,
        PluginCtx {
            logger: PluginLogger {},
            table,
            context: wasi_ctx,
        },
    );

    let component = if plugin_file.is_none() {
        Component::from_binary(&engine, plugin_bytes.unwrap()).expect("could not find plugin")
    } else {
        Component::from_file(&engine, plugin_file.unwrap()).expect("could not find plugin")
    };

    let (plugin, _instance) = Plugin::instantiate_async(&mut store, &component, &linker)
        .await
        .expect("could not instantialte plugin");

    let result = plugin.call_run(&mut store, input, context).await.unwrap();
    result
}

#[cfg(test)]
mod test {
    use super::*;

    const PLUGIN_FILE: &str = "./plugin.wasm";
    #[tokio::test]
    async fn test_call_wasm() {
        let json = r#"
        {
            "name": "张三",
            "age": 33,
            "pet_phrase": [
                "Bond, James Bond.",
                "Shaken, not stirred."
            ]
        }"#;
        // let mut map: HashMap<&str, Value> = HashMap::new();
        // map.insert("id", Value::String("111".to_string()));
        // map.insert("bytes", serde_json::to_value(json).unwrap());
        // let map_param = serde_json::to_vec(&map).unwrap();
        let params: serde_json::value::Value = serde_json::from_str(json).unwrap();
        println!("input: {:#?}", params);
        let result = call_wasm(Some(PLUGIN_FILE), None, json.as_bytes(), None);
        println!(
            "output: {:#?}",
            String::from_utf8_lossy(&result.await.unwrap())
        );
    }
}

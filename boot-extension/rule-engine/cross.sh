#!/bin/bash

#export CROSS_CONTAINER_ENGINE=podman

# install cross
cargo install cross

export RUST_BACKTRACE=1

# x86_64-unknown-linux-gnu
echo "compiling for x86_64-unknown-linux-gnu"
rustup target add x86_64-unknown-linux-gnu
cross build --release --target x86_64-unknown-linux-gnu
ls -la target/x86_64-unknown-linux-gnu/release
cp -f target/x86_64-unknown-linux-gnu/release/libzen_engine.so ./src/main/resources/libzen_engine-linux-x86_64.so

# x86_64-apple-darwin
echo "compiling for x86_64-apple-darwin"
rustup target add x86_64-apple-darwin
cross build --release --target x86_64-apple-darwin
ls -la target/x86_64-apple-darwin/release
cp target/x86_64-apple-darwin/release/libzen_engine.dylib ./src/main/resources/libzen_engine-osx-x86_64.dylib



# aarch64-unknown-linux-gnu
echo "compiling for aarch64-unknown-linux-gnu"
rustup target add aarch64-unknown-linux-gnu
cross build --release --target aarch64-unknown-linux-gnu
ls -la target/x86_64-unknown-linux-gnu/release
cp -f target/x86_64-unknown-linux-gnu/release/libzen_engine.so ./src/main/resources/libzen_engine-linux-aarch_64.so

## arm64-apple-darwin
echo "compiling for aarch64-apple-darwin"
rustup target add aarch64-apple-darwin
cross build --release --target aarch64-apple-darwin
ls -la target/aarch64-apple-darwin/release
cp -f target/aarch64-apple-darwin/release/libzen_engine.dylib ./src/main/resources/libzen_engine-osx-aarch_64.dylib


# x86_64-pc-windows-gnu
echo "compiling for x86_64-pc-windows-gnu"
rustup target add x86_64-pc-windows-msvc
cross build --release --target x86_64-pc-windows-msvc
ls -la target/x86_64-pc-windows-gnu/release
cp -f target/x86_64-pc-windows-gnu/release/zen_engine.dll ./src/main/resources/zen_engine-windows-x86_64.dll







# x86_64-pc-windows-gnu
#echo "compiling for x86_64-pc-windows-gnu"
#rustup target add x86_64-pc-windows-gnu
#cross build --release --target x86_64-pc-windows-gnu
#ls -la target/x86_64-pc-windows-gnu/release
#cp -f target/x86_64-pc-windows-gnu/release/zen_engine.dll ./src/main/resources/zen_engine-x86_64.dll

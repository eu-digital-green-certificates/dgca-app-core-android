/*
 *  ---license-start
 *  eu-digital-green-certificates / dgca-verifier-app-android
 *  ---
 *  Copyright (C) 2021 T-Systems International GmbH and all other contributors
 *  ---
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ---license-end
 *
 *  Created by Mykhailo Nester on 4/23/21 9:49 AM
 */

object Deps {

    const val tools_gradle_android = "com.android.tools.build:gradle:${Versions.gradle}"
    const val tools_kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val desugar_jdk_libs = "com.android.tools:desugar_jdk_libs:${Versions.desugar_jdk_libs}"

    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val kotlin_reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin_reflect}"
    const val java_cose = "com.augustcellars.cose:cose-java:${Versions.java_cose}"
    const val json_validation = "com.github.fge:json-schema-validator:${Versions.json_validation}"
    const val json_validation_rhino = "io.apisense:rhino-android:${Versions.json_validation_rhino}"
    const val jackson_cbor = "com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:${Versions.jackson_cbor}"
    const val bouncy_castle = "org.bouncycastle:bcpkix-jdk15to18:${Versions.bouncy_castle}"

    const val test_junit = "junit:junit:${Versions.junit}"
    const val test_junit_jupiter_api = "org.junit.jupiter:junit-jupiter-api:${Versions.junit_jupiter}"
    const val test_junit_jupiter_params = "org.junit.jupiter:junit-jupiter-params:${Versions.junit_jupiter}"
    const val test_runtime_only = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit_jupiter}"
    const val test_hamcrest = "org.hamcrest:hamcrest:${Versions.hamcrest}"
}
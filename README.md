<h1 align="center">
    EU Digital COVID Certificate App Core - Android
</h1>

<p align="center">
    <a href="/../../commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/eu-digital-green-certificates/dgca-app-core-android?style=flat"></a>
    <a href="/../../issues" title="Open Issues"><img src="https://img.shields.io/github/issues/eu-digital-green-certificates/dgca-app-core-android?style=flat"></a>
    <a href="./LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg?style=flat"></a>
</p>

<p align="center">
  <a href="#about">About</a> •
  <a href="#development">Development</a> •
  <a href="#documentation">Documentation</a> •
  <a href="#support-and-feedback">Support</a> •
  <a href="#how-to-contribute">Contribute</a> •
  <a href="#contributors">Contributors</a> •
  <a href="#licensing">Licensing</a>
</p>

## About

This repository contains the source code of the EU Digital COVID Certificate App Core for Android.

The app core provides shared functionality for the [verifier](https://github.com/eu-digital-green-certificates/dgca-verifier-app-android) and [wallet](https://github.com/eu-digital-green-certificates/dgca-wallet-app-android) apps.

Base module that provides functionality for handling DCC certificate type. It decodes the base45-encoded QR code, extracts the COSE signature, and decodes CBOR back to JSON. It then verifies the signature with the keys provided by the verifier/wallet app’s backend. The module uses only open-source libraries.

## Documentation

Module features:
- Prefix validation: Drops a country-specific prefix from contents, e.g. "HC1:"
- Base45 Decoding: decodes provided input according to specification: [Base45](https://datatracker.ietf.org/doc/draft-faltstrom-base45/?include_text=1)
- Decompression with ZLIB
- Decodes input according to COSE specification [RFC8152](https://datatracker.ietf.org/doc/html/rfc8152)
- Decodes input as a [CBOR structure](https://datatracker.ietf.org/doc/html/rfc7049)
- Schema validation - verifies CBOR with predefined schema (JsonSchema.kt#JSON_SCHEMA_V1)
- Verifies COSE signature

For more details check unit tests to see how it works step by step.

## Support and feedback

The following channels are available for discussions, feedback, and support requests:

| Type                     | Channel                                                |
| ------------------------ | ------------------------------------------------------ |
| **Issues**    | <a href="/../../issues" title="Open Issues"><img src="https://img.shields.io/github/issues/eu-digital-green-certificates/dgca-app-core-android?style=flat"></a>  |
| **Other requests**    | <a href="mailto:opensource@telekom.de" title="Email DGC Team"><img src="https://img.shields.io/badge/email-DGC%20team-green?logo=mail.ru&style=flat-square&logoColor=white"></a>   |

## How to contribute  

Contribution and feedback is encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](./CONTRIBUTING.md). By participating in this project, you agree to abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## Contributors  

Our commitment to open source means that we are enabling -in fact encouraging- all interested parties to contribute and become part of its developer community.

## Licensing

Copyright (C) 2021 T-Systems International GmbH and all other contributors

Licensed under the **Apache License, Version 2.0** (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.

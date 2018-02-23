# cljs-iota

[![Clojars Project](https://img.shields.io/clojars/v/cljs-iota.svg)](https://clojars.org/cljs-iota)

ClojureScript API for [IOTA](https://iota.org/) Ledger's [JavaScript library](https://github.com/iotaledger/iota.lib.js/).

## Status

Implemented all methods.

TODO Standard API (`api.cljs`):
- `get-trytes`, only returns 9s
- Fix methods that return "Invalid Trytes provided".

TODO:
- Use [tools.deps](https://github.com/clojure/tools.deps.alpha) instead of lein?
- Add `valid-transaction?` and `valid-bundle?` test util methods
- Move test data to separate namespace
- clojure.spec everything

TODO:
Add iris like MAM?


## Installation

```clojure
(ns my.app
  (:require [cljs-iota.api :as iota-api]
            [cljs-iota.core :as iota]
            [cljs-iota.multisig :as iota-multisig]
            [cljs-iota.utils :as iota-utils]
            [cljs-iota.valid :as iota-valid]))
```

## Usage
Stick with the IOTA JavaScript API [docs](https://github.com/iotaledger/iota.lib.js#iotaapi), all methods there have their kebab-cased version in this library. Also, return values and responses in callbacks are automatically kebab-cased and keywordized. Instead of calling a method on the IOTA object, you pass it as a first argument. For example:

```javascript
iota.api.getTransactionsObjects(hashes, callback)
iota.utils.convertUnits(value, fromUnit, toUnit)
iota.multisig.getKey(seed, index, security)
iota.valid.isAddress(address)
```
becomes

```clojure
(iota-api/get-transaction-objects hashes (fn [err res]))
(iota-utils/convert-units value from-unit to-unit)
(iota-multisig/get-key seed index security)
(iota-valid/address? address)
```

Docstrings for the methods and namespaces are adjusted to ClojureScript from the [IOTA JavaScript library](https://github.com/iotaledger/iota.lib.js#iota-javascript-library).

When they are missing see the [IOTA documentation](https://iota.readme.io/v1.2.0/reference).

## Acknowledgements

This IOTA library uses the same wrapper methods as the [ClojureScript API for Ethereum Web3 API by district0x](https://github.com/district0x/cljs-web3).

# cljs-iota

ClojureScript API for [IOTA](https://iota.org/) Ledger's [JavaScript library](https://github.com/iotaledger/iota.lib.js/).

## Status

Implemented core functions and Standard API.

TODO Standard API (`api.cljs`):
- `get-trytes`, only returns 9s
- `attach-to-tangle`, `broadcast-transactions`, and `store-transactions`
  return "Invalid Trytes provided".

TODO multisig (`multisig.cljs`):
- All methods and docstrings.

TODO utils (`utils.cljs`):
- All methods and docstrings.

TODO valid (`valid.cljs`):
- All methods and docstrings.

TODO:
- More random `generate-seed` method in `core.cljs`.
- Use [tools.deps](https://github.com/clojure/tools.deps.alpha) instead of lein
- clojure.spec everything


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

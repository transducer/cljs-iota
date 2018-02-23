(ns cljs-iota.valid
  "Validator functions that can help with determining whether the inputs or
  results that you get are valid.

  Validator functions. Return either true or false."
  (:require [cljs-iota.js-utils :as js-utils]))


(defn- valid
  "Gets valid object from IOTA client.

  Parameter:
  iota - IOTA client instance"
  [iota]
  (aget iota "valid"))


(defn address?
  "Checks if the provided input is a valid 81-tryte (non-checksum), or
  90-tryte (with checksum) address.

  Arguments:
  iota - IOTA client instance
  address - string A single address"
  [iota address]
  (js-utils/js-apply (valid iota) "isAddress" [address]))


(defn trytes?
  "Determines if the provided input is valid trytes. Valid trytes are:
  ABCDEFGHIJKLMNOPQRSTUVWXYZ9. If you specify the length parameter, you can also
  validate the input length.

  Arguments:
  iota - IOTA client instance
  trytes - string
  length - int || string optional"
  ([iota address]
   (js-utils/js-apply (valid iota) "isTrytes" [address]))
  ([iota address length]
   (js-utils/js-apply (valid iota) "isTrytes" [address length])))


(defn value?
  "Validates the value input, checks if it's integer.

  Arguments:
  iota - IOTA client instance
  value - integer"
  [iota value]
  (js-utils/js-apply (valid iota) "isValue" [value]))


(defn num?
  "Checks if the input value is a number, can be a string, float or integer.

  NOTE: Negative values are not numbers in this implementation.

  Arguments:
  iota - IOTA client instance
  value - integer"
  [iota value]
  (js-utils/js-apply (valid iota) "isNum" [value]))


(defn hash?
  "Checks if correct hash consisting of 81-trytes.

  Arguments:
  iota - IOTA client instance
  hash - string"
  [iota hash]
  (js-utils/js-apply (valid iota) "isHash" [hash]))


(defn transfers?
  "Checks if it's a correct coll of transfer maps. A transfer map
  consists of the following keys and values:

  {:address ;; string (trytes encoded, 81 or 90 trytes)
   :value   ;; int
   :message ;; string (trytes encoded)
   :tag     ;; string (trytes encoded, maximum 27 trytes)}

  Arguments:
  iota - IOTA client instance
  transfers: coll "
  [iota transfers]
  (js-utils/js-apply (valid iota) "isTransfersArray" [transfers]))


(defn hashes?
  "Coll of valid 81 or 90-trytes hashes.

  Arguments:
  iota - IOTA client instance
  hashes: coll"
  [iota hashes]
  (js-utils/js-apply (valid iota) "isArrayOfHashes" [hashes]))


(defn trytes-coll?
  "Checks if it's a coll of correct 2673-trytes. These are trytes either
  returned by `prepare-transfers`, `attach-to-tangle` or similar call. A single
  transaction object is encoded 2673 trytes.

  Arguments:
  iota - IOTA client instance
  trytes-coll: coll of trytes"
  [iota trytes-coll]
  (js-utils/js-apply (valid iota) "isArrayOfTrytes" [trytes-coll]))


(defn attached-trytes-coll?
  "Similar to `trytes-coll?`, just that in addition this function also validates
  that the last 243 trytes are non-zero (meaning that they don't equal 9). The
  last 243 trytes consist of: `trunk-transaction` + `branch-transaction` +
  nonce. As such, this function determines whether the provided trytes have been
  attached to the tangle successfully. For example this validator can be used
  for trytes returned by `attach-to-tangle`.

  Arguments:
  iota - IOTA client instance
  trytes-coll: coll of trytes"
  [iota trytes-coll]
  (js-utils/js-apply (valid iota) "isArrayOfAttachedTrytes" [trytes-coll]))


(defn transactions?
  "Checks if the provided bundle is an array of correct transaction objects.
  Basically validates if each entry in the array has all of the following keys:

  ```
  :hash
  :signature-message-fragment
  :address
  :value
  :tag
  :timestamp
  :current-index
  :last-index
  :bundle
  :trunk-transaction
  :branch-transaction
  :nonce
  ```

  Arguments:
  iota - IOTA client instance
  bundle - coll"
  [iota bundle]
  (js-utils/js-apply (valid iota) "isArrayOfTxObjects" [bundle]))


(defn inputs?
  "Validates if it's a coll of correct inputs. These inputs are provided to
  either `prepare-transfers` or `send-transfer`. An input map consists of
  the following:

  ```
  {:key-index ;; int
   :address   ;; string}
  ```

  Arguments:
  iota - IOTA client instance
  inputs - coll"
  [iota inputs]
  (js-utils/js-apply (valid iota) "isInputs" [inputs]))


(defn iota-string?
  "Self explanatory. Not similar to `core/string?`. E.g., char is string.

  Arguments:
  iota - IOTA client instance
  s - string"
  [iota s]
  (js-utils/js-apply (valid iota) "isString" [s]))


(defn iota-coll?
  "Self explanatory.

  Arguments:
  iota - IOTA client instance
  c - coll"
  [iota c]
  (js-utils/js-apply (valid iota) "isArray" [c]))


(defn iota-obj?
  "Self explanatory. Only clojurized. Clojure data structurs are iota objects.

  Arguments:
  iota - IOTA client instance
  obj - object"
  [iota obj]
  (js-utils/js-apply (valid iota) "isObject" [obj]))


(defn iota-uri?
  "Validates a given string to check if it's a valid IPv6, IPv4 or hostname format.
  The string must have a `udp://` prefix, and it may or may not have a port. Here
  are some example inputs:

  udp://[2001:db8:a0b:12f0::1]:14265
  udp://[2001:db8:a0b:12f0::1]
  udp://8.8.8.8:14265
  udp://domain.com
  udp://domain2.com:14265

  Arguments:
  iota - IOTA client instance
  node - string IPv6, IPv4 or Hostname with or without a port.

  Returns bool - true / false if valid node format."
  [iota node]
  (js-utils/js-apply (valid iota) "isUri" [node]))

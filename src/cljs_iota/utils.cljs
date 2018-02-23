(ns cljs-iota.utils
  "Utility related functions for conversions, validation and so on.
  All utils function are done synchronously."
  (:require [cljs-iota.js-utils :as js-utils]
            [clojure.string :as string]))


(defn utils
  "Gets utils object from IOTA library instance.

  Parameter:
  iota - IOTA client instance"
  [iota]
  (aget iota "utils"))


(defn convert-units
  "IOTA utilizes the Standard system of Units. See below for all available
  units:

  :i   :   1,
  :ki  :   1000,
  :mi  :   1000000,
  :gi  :   1000000000,
  :ti  :   1000000000000,
  :pi  :   1000000000000000

  Arguments:
  iota - IOTA client instance
  value - number || string Value to be converted. Can be string, an integer or
                           float (a number).
  from-unit - keyword Current unit of the value. See above for the available units
              to utilize for conversion.
  to-unit - keyword Unit to convert the from value into

  Returns the converted unit (from-unit => to-unit). "
  [iota value from-unit to-unit]
  (letfn [(stringify-unit [k]
            (if (= (count (name k)) 1)
              (name k)
              (string/capitalize (name k))))]
    (js-utils/js-apply (utils iota) "convertUnits"
                       [value
                        (stringify-unit from-unit)
                        (stringify-unit to-unit)])))


(defn add-checksum
  "Takes a tryte-encoded input value and adds a checksum (length is user
  defined). Standard checksum length is 9 trytes. If `is-address` is defined as
  true, it will validate if it's a correct 81-tryte encoded address.

  Arguments:
  iota - IOTA client instance
  input-value - string | coll Either an individual tryte value, or a coll of
                              tryte values.
  :checksum-length - int Checksum length. Default is 9 trytes
  :is-address - bool indicates whether the input value should be validated as an
                     address (81-trytes). Default is true.

  Returns the input value + checksum either as a string or coll, depending on
  the input."
  [iota input-value & {:keys [checksum-length is-address]
                       :or   {checksum-length 9 is-address true}
                       :as   opts}]
  (js-utils/js-apply (utils iota)
                     "addChecksum"
                     [input-value checksum-length is-address]))


(defn no-checksum
  "Takes an 90-trytes address as input and simply removes the checksum.

  Arguments:
  iota - IOTA client instance
  address - string | coll 90-trytes address. Either string or a coll.

  Returns string | coll - returns the 81-tryte address(es)"
  [iota address]
  (js-utils/js-apply (utils iota) "noChecksum" [address]))


(defn valid-checksum?
  "Takes an 90-trytes checksummed address and returns a true / false if it is
  valid.

  Arguments:
  iota - IOTA client instance
  address-with-checksum - String 90-trytes address

  Returns true or false whether the checksum is valid or not."
  [iota address-with-checksum]
  (js-utils/js-apply (utils iota) "isValidChecksum" [address-with-checksum]))


(defn transaction-object
  "Converts the trytes of a transaction into its transaction object.

  iota - IOTA client instance
  trytes: string 2673-trytes of a transaction

  Returns transaction map."
  [iota trytes]
  (js-utils/js-apply (utils iota) "transactionObject" [trytes]))


(defn transaction-trytes
  "Converts a valid transaction object into trytes. Please refer to
  https://domschiener.gitbooks.io/iota-guide/content/chapter1/transactions-and-bundles.html
  for more information what a valid transaction object looks like.

  Arguments:
  iota - IOTA client instance
  transaction-object: Object valid transaction object

  Returns converted trytes of transaction."
  [iota transaction]
  (js-utils/js-apply (utils iota) "transactionTrytes" [transaction]))


(defn categorize-transfers
  "Categorizes a list of transfers into sent and received. It is important to
  note that zero value transfers (which are, for example, used for storing
  addresses in the Tangle), are seen as received in this function.

  Arguments:
  iota - IOTA client instance
  transfers - coll A coll of bundles. Basically is an array, of arrays (bundles),
                   as is returned from `get-transfers` or `get-account-data`
  addresses - coll of addresses that belong to you. With these addresses as
              input, it's determined whether it's a sent or a receive transaction.
              Therefore make sure that these addresses actually belong to you.

  Returns map of the transfers categorized into :sent and :received"
  [iota transfers addresses]
  (js-utils/js-apply (utils iota) "categorizeTransfers" [transfers addresses]))


(defn to-trytes
  "Converts ASCII characters into trytes according to our encoding schema (read
  the source code for more info as to how it works). Currently only works with
  valid ASCII characters. As such, if you provide invalid characters the
  function will return `nil`.

  Arguments:
  iota - IOTA client instance
  input - String you want to convert into trytes. All non-string values
          should be converted into strings first.

  Returns string || nil - trytes, or nil in case you provided an invalid ASCII
  character"
  [iota input]
  (js-utils/js-apply (utils iota) "toTrytes" [input]))


(defn from-trytes
  "Reverse of toTrytes.

  Input
  iota - IOTA client instance
  trytes - string Trytes you want to convert to string

  Returns string - converted string"
  [iota input]
  (js-utils/js-apply (utils iota) "fromTrytes" [input]))


(defn extract-json
  "This function takes a bundle as input and from the
  `signature-message-fragments` extracts the JSON encoded data which was sent
  with the transfer. This currently only works with the `to-trytes` and
  `from-trytes` function that use the ASCII <-> Trytes encoding scheme. In case
  there is no JSON data, or invalid one, this function will return nil.

  Arguments:
  iota - IOTA client instance
  bundle - Array bundle from which you want to extract the JSON data.

  Returns stringified JSON object which was extracted from the transactions."
  [iota bundle]
  (js-utils/js-apply (utils iota) "extractJson" [bundle]))


(defn validate-signatures
  "This function makes it possible for each of the co-signers in the
  multi-signature to independently verify that a generated transaction with the
  corresponding signatures of the co-signers is valid. This function is safe to
  use and does not require any sharing of digests or key values.

  Arguments:
  iota - IOTA client instance
  signed-bundle - coll signed bundle by all of the co-signers
  input-address - string input address as provided to `initiate-transfer`

  Returns bool - true / false"
  [iota signed-bundle input-address]
  (js-utils/js-apply (utils iota)
                     "validateSignatures"
                     [signed-bundle input-address]))


(defn bundle?
  "Checks if the provided bundle is valid. The provided bundle has to be
  ordered tail (i.e. current-index: 0) first. A bundle is deemed valid if it has:

  - Valid transaction structure
  - Correct current-index, last-index and number of bundle transactions
  - The sum of all value fields is 0
  - The bundle hash is correct
  - Valid signature

  Arguments:
  iota - IOTA client instance
  bundle: Array bundle to test

  Returns bool - true / false"
  [iota bundle]
  (js-utils/js-apply (utils iota) "isBundle" [bundle]))

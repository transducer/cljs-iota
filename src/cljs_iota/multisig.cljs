(ns cljs-iota.multisig
  "Functions for creating and signing multi-signature addresses and
  transactions.

  VERY IMPORTANT NOTICE

  Before using these functions, please make sure that you have thoroughly read
  our guidelines for multi-signature at
  https://github.com/iotaledger/wiki/blob/master/multisigs.md. It is of utmost
  importance that you follow these rules, else it can potentially lead to
  financial losses."
  (:require [cljs-iota.js-utils :as js-utils]))


(defn- multisig
  "Gets multisig object from IOTA client.

  Parameter:
  iota - IOTA client instance"
  [iota]
  (aget iota "multisig"))


(defn get-key
  "Generates the corresponding private key (depending on the security chosen)
  of a seed.

  Arguments
  iota - IOTA client instance
  seed - string Tryte encoded seed
  index - int Index of the private key.
  security-level - int Security level to be used for the private key

  Returns string - private key represented in trytes."
  [iota seed index security-level]
  {:pre [(<= 1 security-level 3)]}
  (js-utils/js-apply (multisig iota) "getKey" [seed index security-level]))


(defn get-digest
  "Generates the digest value of a key.

  Arguments
  iota - IOTA client instance
  seed - string Tryte encoded seed
  index - int Index of the private key.
  security-level - int Security level to be used for the private key

  Returns string - digest represented in trytes."
  [iota seed index security-level]
  {:pre [(<= 1 security-level 3)]}
  (js-utils/js-apply (multisig iota) "getDigest" [seed index security-level]))


(defn address
  "This function is used to initiate the creation of a new multisig address.
  Once all digests were added with `add-digest`, `finalize` can be used to get
  the actual 81-tryte address value. `validate-address` can be used to actually
  validate the multi-signature.

  Arguments:
  iota - IOTA client instance
  digest-trytes: string || coll Optional string or array of digest trytes as
                                returned by `get-digest`

  Returns Object - multisig address instance"
  [iota digest-trytes]
  (js-utils/js-apply (multisig iota) "address" [digest-trytes]))


(defn absorb
  "Absorbs the digests of co-signers

  Arguments:
  address - IOTA multisig address instance
  digest: string || coll String or coll of digest trytes as returned by
                         `get-digest`

  Returns Object - multisig address instance"
  [address digest]
  (js-utils/js-apply address "absorb" [digest]))


(defn finalize
  "Finalizes the multisig address generation process and returns the correct
  81-tryte address.

  Arguments:
  address - IOTA multisig address instance

  Returns string - 81-tryte multisig address"
  [address]
  (js-utils/js-apply address "finalize"))


(defn validate-address
  "Validates a generated multi-sig address by getting the corresponding key
  digests of each of the co-signers. The order of the digests is of essence in
  getting correct results.

  Arguments:
  iota - IOTA client instance
  multisig-address - string digest trytes as returned by `get-digest`
  digests - coll of the key digest for each of the cosigners. The digests need
            to be provided in the correct signing order.

  Returns bool - true / false"
  [iota multisig-address digests]
  (js-utils/js-apply (multisig iota) "validateAddress"
                     [multisig-address digests]))


(defn initiate-transfer
  "Initiates the creation of a new transfer by generating an empty bundle with
  the correct number of bundle entries to be later used for the signing process.
  It should be noted that currently, only a single input (via `input-address`)
  is possible. The `remainder-address` also has to be provided and should be
  generated by the co-signers of the multi-signature before initiating the
  transfer.

  The `security-sum` input is basically the sum of the security levels from all
  cosigners chosen during the private key generation (`get-key` / `get-digest`).
  e.g. when creating a new multisig, Bob has chosen security level 2, whereas
  Charles has chosen security level 3. Their `security-sum` is 5.

  Arguments:
  input, map which contains:
          `:address` is the input multisig address which has sufficient balance
                     and is controlled by the co-signers
          `:security-sum` is the sum of security levels used by all co-signers
          `:balance` is the expected balance, if you wish to override
                    `get-balances`
  remainder-address - string in case there is a remainder balance, send the
                      funds to this address. If you do not have a remainder
                      balance, you can simply put nil
  transfers - coll Transfers
  f - fn callback function

  Returns bundle coll."
  [iota input remainder-address transfers f]
  (js-utils/js-apply (multisig iota)
                     "initiateTransfer"
                     [input remainder-address transfers f]))


(defn add-signature
  "This function is called by each of the co-signers individually to add their
  signature to the bundle. Here too, order is important. This function returns
  the bundle, which should be shared with each of the participants of the
  multi-signature.

  After having added all signatures, you can validate the signature with the
  `validate-signature` function.

  bundle-to-sign: coll bundle to sign
  input-address: string input address as provided to `initiate-transfer`.
  private-key: String private key trytes as returned by `get-key`
  callback: fn Function

  Returns coll - bundle"
  [iota bundle-to-sign input-address private-key f]
  (js-utils/js-apply (multisig iota)
                     "addSignature"
                     [bundle-to-sign input-address private-key f]))

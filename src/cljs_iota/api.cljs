(ns cljs-iota.api
  "Core API functionality for interacting with the IOTA core.

  See https://iota.readme.io/v1.2.0/reference"
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-iota.js-utils :as js-utils]
            [cljs.core.async :as async :refer [>! chan]]))


(defn- api
  "Gets API object from IOTA library instance.

  Parameter:
  iota - IOTA client instance"
  [iota]
  (aget iota "api"))


;;;;
;;;; Standard API

(defn get-node-info
  "Returns information about your node.

  Arguments:
  iota - IOTA client instance
  callback - Callback with error and result

  Return values:
    :app-name - Name of the IOTA software you're currently using (IRI stands for
  Initial Reference Implementation).

    :app-version - The version of the IOTA software you're currently running.
    :jre-available-processes - Available cores on your machine for JRE.
    :jre-free-memory - Returns the amount of free memory in the Java Virtual
  Machine.
    :jre-max-memory - Returns the maximum amount of memory that the Java virtual
  machine will attempt to use.
    :jre-total-memory - Returns the total amount of memory in the Java virtual
  machine.
    :latest-milestone - Latest milestone that was signed off by the coordinator.
    :latest-milestone-index - Index of the latest milestone.
    :latest-solid-subtangle-milestone - The latest milestone which is solid and
  is used for sending transactions. For a milestone to become solid your local
  node must basically approve the subtangle of coordinator-approved
  transactions, and have a consistent view of all referenced transactions.
    :latest-solid-subtangle-milestone-index - Index of the latest solid
  subtangle.
    :neighbors - Number of neighbors you are directly connected with.
    :packets-queue-size - Packets which are currently queued up.
    :time - Current UNIX timestamp.
    :tips - Number of tips in the network.
    :transactions-to-request - Transactions to request during syncing process."
  [iota & args]
  (js-utils/js-apply (api iota) "getNodeInfo" args))


(defn get-neighbors
  "Returns the set of neighbors you are connected with, as well as their
  activity count. The activity counter is reset after restarting IRI.

  Arguments:
  iota - IOTA client instance
  callback - Callback with error and result

  Return Values:

    :address - address of your peer

    :number-of-all-transactions - Number of all transactions sent (invalid,
                                  valid, already-seen)

    :number-of-invalid-transactions - Invalid transactions your peer has sent you.
                                      These are transactions with invalid
                                      signatures or overall schema.
    :number-of-new-transactions - New transactions which were transmitted."
  [iota & args]
  (js-utils/js-apply (api iota) "getNeighbors" args))


(defn add-neighbors
  "Add a list of neighbors to your node. It should be noted that this is only
  temporary, and the added neighbors will be removed from your set of neighbors
  after you relaunch IRI.

  Arguments:
  iota - IOTA client instance
  uris - List of URI elements
  callback - Callback with error and result

  The URI (Unique Resource Identification) for adding neighbors is:
  udp://IPADDRESS:PORT"
  [iota & args]
  (js-utils/js-apply (api iota) "addNeighbors" args))


(defn remove-neighbors
  "Removes a list of neighbors to your node. This is only temporary, and if you
  have your neighbors added via the command line, they will be retained after
  you restart your node.

  Arguments:
  iota - IOTA client instance
  uris - List of URI elements
  callback - Callback with error and result

  The URI (Unique Resource Identification) for adding neighbors is:
  udp://IPADDRESS:PORT"
  [iota & args]
  (js-utils/js-apply (api iota) "removeNeighbors" args))


(defn get-tips
  "Returns the list of tips.

  Arguments:
  iota - IOTA client instance
  callback - Callback with error and result"
  [iota & args]
  (js-utils/js-apply (api iota) "getTips" args))


(defn find-transactions
  "Find the transactions which match the specified input and return. All input
  values are lists, for which a list of return values (transaction hashes), in
  the same order, is returned for all individual elements.

  Arguments:
  iota - IOTA client instance
  request-map - The keys of the map can either be `:bundles`, `:addresses`,
  `:tags` or `:approvees`. The values should be lists. *Using multiple of these
  input fields returns the intersection of the values.*

  | Parameters | Type | Required | Description                          |
  | bundles    | list | Optional | List of bundle hashes.               |
  | addresses  | list | Optional | List of addresses.                   |
  | tags       | list | Optional | List of tags. Has to be 27 trytes.   |
  | approvees  | list | Optional | List of approvee transaction hashes. |

  callback - Callback with error and result


  Return Values:

  The transaction hashes which are returned depend on your input. For each
  specified input value, the command will return the following:

    bundles: returns the list of transactions which contain the specified bundle
  hash.
    addresses: returns the list of transactions which have the specified address
  as an input/output field.
    tags: returns the list of transactions which contain the specified tag
  value.
    approvees: returns the list of transaction which reference (i.e. confirm)
  the specified transaction."
  [iota & args]
  (js-utils/js-apply (api iota) "findTransactions" args))


(defn get-trytes
  "Returns the raw transaction data (trytes) of a specific transaction. These
  trytes can then be easily converted into the actual transaction object. See
  utility functions for more details.

  Arguments:
  iota - IOTA client instance
  hashes -List of transaction hashes of which you want to get trytes from.
  callback - Callback with error and result"
  [iota & args]
  (js-utils/js-apply (api iota) "getTrytes" args))


(defn get-inclusion-states
  "Get the inclusion states of a set of transactions. This is for determining if
  a transaction was accepted and confirmed by the network or not. You can search
  for multiple tips (and thus, milestones) to get past inclusion states of
  transactions.

  This API call simply returns a list of boolean values in the same order as the
  transaction list you submitted, thus you get a true/false whether a
  transaction is confirmed or not.

  Arguments:
  iota - IOTA client instance
  transactions - List of transactions you want to get the inclusion state for.
  tips - List of tips (including milestones) you want to search for the
  inclusion state.
  callback - Callback with error and result"
  [iota & args]
  (js-utils/js-apply (api iota) "getInclusionStates" args))


(defn get-balances
  "Similar to `get-inclusion-states`. It returns the confirmed balance which a
  list of addresses have at the latest confirmed milestone. In addition to the
  balances, it also returns the milestone as well as the index with which the
  confirmed balance was determined. The balances is returned as a list in the
  same order as the addresses were provided as input.

  Arguments:
  iota - IOTA client instance
  addresses - List of addresses you want to get the confirmed balance from
  threshold - Confirmation threshold, should be set to 100.
  callback - Callback with error and result"
  [iota & args]
  (js-utils/js-apply (api iota) "getBalances" args))


(defn get-transactions-to-approve
  "Tip selection which returns :trunk-transaction and :branch-transaction. The
  input value is depth, which basically determines how many bundles to go back
  to for finding the transactions to approve. The higher your depth value, the
  more \"babysitting\" you do for the network (as you have to confirm more
  transactions).

  Arguments:
  iota - IOTA client instance
  depth - Number of bundles to go back to determine the transactions for
  approval.
  reference - the tips you are going to reference with an approved transaction?
  callback - Callback with error and result"
  [iota & args]
  (js-utils/js-apply (api iota) "getTransactionsToApprove" args))


(defn attach-to-tangle
  "Attaches the specified transactions (trytes) to the Tangle by doing Proof of
  Work. You need to supply branch-transaction as well as
  trunk-transaction (basically the tips which you're going to validate and
  reference with this transaction) - both of which you'll get through the
  `get-transactions-to-approve` API call.

  The returned value is a different set of tryte values which you can input into
  `broadcast-transactions` and `store-transactions`. The returned tryte value,
  the last 243 trytes basically consist of the: trunk-transaction +
  branch-transaction + nonce. These are valid trytes which are then accepted by
  the network.

  Arguments:
  iota - IOTA client instance
  trunk-transaction - Trunk transaction to approve.
  branch-transaction - Branch transaction to approve.
  min-weight-magnitude - Proof of Work intensity. Minimum value is `18`.
  trytes - List of trytes (raw transaction data) to attach to the tangle.
  callback - Callback with error and result."
  [iota & args]
  (js-utils/js-apply (api iota) "attachToTangle" args))


(defn interrupt-attaching-to-tangle
  "Interrupts and completely aborts the `attach-to-tangle` process."
  [iota & args]
  (js-utils/js-apply (api iota) "interruptAttachingToTangle" args))


(defn broadcast-transactions
  "Broadcast a list of transactions to all neighbors. The input trytes for this
  call are provided by `attach-to-tangle`.

  Arguments:
  iota - IOTA client instance
  trytes - List of raw data of transactions to be rebroadcast
  callback - Callback with error and result"
  [iota & args]
  (js-utils/js-apply (api iota) "broadcastTransactions" args))


(defn store-transactions
  "Store transactions into the local storage. The trytes to be used for this call
  are returned by `attach-to-tangle`.

  Arguments:
  iota - IOTA client instance
  trytes - List of raw data of transactions to be rebroadcast.
  callback - Callback with error and result"
  [iota & args]
  (js-utils/js-apply (api iota) "storeTransactions" args))


;;;;
;;;; JavaScript API

(defn get-transactions-objects
  "Wrapper function for `get-trytes` and the Utility function
  `transaction-objects.` This function basically returns the entire transaction
  objects for a list of transaction hashes.

  Arguments:
  iota - IOTA client instance
  hashes - List of transaction hashes
  callback - Callback with error and result

  Returns list of all the transaction objects from the corresponding hashes."
  [iota & args]
  (js-utils/js-apply (api iota) "getTransactionsObjects" args))


;;; `find-transaction-objects` behaves as `find-transactions`, alias included

(def find-transaction-objects find-transactions)


(defn get-latest-inclusion
  "Wrapper function for `get-node-info` and `get-inclusion-states`. It simply
  takes the most recent solid milestone as returned by `get-node-info`, and uses
  it to get the inclusion states of a list of transaction hashes.

  Arguments:
  iota - IOTA client instance
  hashes - Array List of transaction hashes
  callback - Function callback with error and result

  Returns list of all the inclusion states of the transaction hashes."
  [iota & args]
  (js-utils/js-apply (api iota) "getLatestInclusion" args))


;;; Mentioned in iota.lib.js README, but not implemented on API object

#_(defn broadcast-and-store
  "Wrapper function for `broadcast-transactions` and `store-transactions`.

  Arguments:
  iota - IOTA client instance
  trytes: List of transaction trytes to be broadcast and stored. Has to be
          trytes that were returned from `attach-to-tangle`
  callback: Function callback with error and result parameters.

  Returns empty map."
    [iota & args]
  (js-utils/js-apply (api iota) "broadcastAndStore" args))


(defn get-new-address
  "Generates a new address from a seed and returns the address. This is either
  done deterministically, or by providing the index of the new address to be
  generated. When generating an address, you have the option to choose different
  security levels for your private keys. A different security level with the
  same key index, means that you will get a different address obviously (as
  such, you could argue that single seed has 3 different accounts, depending on
  the security level chosen).

  In total, there are 3 different security options available to choose from:
  Input 	Security Level 	Security
  1 	    Low 	          81-trits
  2 	    Medium 	        162-trits
  3 	    High 	          243-trits

  Arguments:
  iota - IOTA client instance

  seed - tryte-encoded seed. It should be noted that this seed is not
         transferred
  options - Map which is optional with following keys:
    :index - If the index is provided, the generation of the address is not
             deterministic.

    :checksum - true or false - Adds 9-tryte address checksum
    :total - Total number of addresses to generate.
    :security - Security level to be used for the private key / address. Can be
                1, 2 or 3
    :return-all - If true, it returns all addresses which were deterministically
                  generated (until `find-transactions` returns nil)
  callback - Optional function callback with error and result.

  Returns either a string, or an array of strings."
  [iota & args]
  (js-utils/js-apply (api iota) "getNewAddress" args))


(defn get-inputs
  "Gets all possible inputs of a seed and returns them with the total balance.
  This is either done deterministically (by genearating all addresses until
  `find-transactions` returns null for a corresponding address), or by providing a
  key range to use for searching through.

  You can also define the minimum `threshold` that is required. This means that if
  you provide the `threshold` value, you can specify that the inputs should only
  be returned if their collective balance is above the threshold value.

  Arguments:
  iota - IOTA client instance
  seed - tryte-encoded seed. It should be noted that this seed is not transferred
  options - optional map with follows keys:
    :start - int Starting key index
    :end - int Ending key index
    :security - Int Security level to be used for the private key / address. Can
                be 1, 2 or 3
    :threshold - int Minimum threshold of accumulated balances from the inputs
                 that is requested
  callback - optional callback.

  Return a map with the following keys:
  :inputs - list of inputs objects consisting of `address`, `balance` and
            `key-index`
  :total-balance - int aggregated balance of all inputs"
  [iota & args]
  (js-utils/js-apply (api iota) "getInputs" args))


(defn prepare-transfers
  "Main purpose of this function is to get an array of transfer objects as
  input, and then prepare the transfer by generating the correct bundle, as well
  as choosing and signing the inputs if necessary (if it's a value transfer).
  The output of this function is an array of the raw transaction data (trytes).

  You can provide multiple transfer objects, which means that your prepared
  bundle will have multiple outputs to the same, or different recipients. As
  single transfer object takes the values of: `address`, `value`, `message`,
  `tag`. The message and tag values are required to be tryte-encoded. If you do
  not supply a message or a tag, the library will automatically enter empty ones
  for you. As such the only required fields in each transfers object are `address`
  and value.

  If you provide an address with a checksum, this function will automatically
  validate the address for you with the utils function `is-valid-checksum`.

  For the options, you can provide a list of inputs, that will be used for
  signing the transfer's inputs. It should be noted that these inputs (an array
  of objects) should have the provided 'security', `key-index` and `address`
  values:

  ```
  [{:key-index val
    :address val
    :security val}]
  ```

  The library validates these inputs then and ensures that you have sufficient
  balance. When defining these inputs, you can also provide multiple inputs on
  different security levels. The library will correctly sign these inputs using
  your seed and the corresponding private keys. Here is an example using
  security level 3 and 2 for a transfer:

  ```
  (prepare-transfers
    iota
    seed
    [{:address \"SSEWOZSDXOVIURQRBTBDLQXWIXOLEUXHYBGAVASVPZ9HBTYJJEWBR9PDTGMXZGKPTGSUDW9QLFPJHTIEQZNXDGNRJE\"
      :value   10000}]
    {:inputs
     [{:address   \"XB9IBINADVMP9K9FEIIR9AYEOFUU9DP9EBCKOTPSDVSNRRNVSJOPTFUHSKSLPDJLEHUBOVEIOJFPDCZS9\"
       :balance   1500
       :key-index 0
       :security  3}
      {:address  \"W9AZFNWZZZNTAQIOOGYZHKYJHSVMALVTWJSSZDDRVEIXXWPNWEALONZLPQPTCDZRZLHNIHSUKZRSZAZ9W\"
       :balance  8500
       :key-index 7
       :security 2}]}
    (fn [err res] (println err res)))
  ```

  The `address` option can be used to define the address to which a remainder
  balance (if that is the case), will be sent to. So if all your inputs have a
  combined balance of 2000, and your spending 1800 of them, 200 of your tokens
  will be sent to that remainder address. If you do not supply the `address`, the
  library will simply generate a new one from your seed (taking `security` into
  account, or using the standard security value of 2 (medium)).

  Arguments:
  iota - IOTA client instance

  seed - string tryte-encoded seed. It should be noted that this seed is not
         transferred
  transfers-array - List of transfer objects:
    :address - 81-tryte encoded address of recipient
    :value - int value to be transferred.
    :message - tryte-encoded message to be included in the bundle.
    :tag - Tryte-encoded tag. Maximum value is 27 trytes.
  options - map which is optional, keys:
  :inputs - List of inputs used for funding the transfer
  :address - string if defined, this address will be used for sending the
             remainder value (of the inputs) to.
  :security - int Security level to be used for the private key / addresses.
              This is for inputs and generating of the remainder address in
              case you did not specify it. Can be 1, 2 or 3
  callback - optional callback.

  Returns an array that contains the trytes of the new bundle."
  [iota & args]
  (js-utils/js-apply (api iota) "prepareTransfers" args))


(defn send-trytes
  "Wrapper function that does `attach-to-tangle` and finally, it broadcasts and
  stores the transactions.

  Arguments:
  iota - IOTA client instance
  trytes - vector with trytes
  depth - int depth value that determines how far to go for tip selection
  min-weight-magnitude - int minimum weight magnitude
  callback - Function Optional callback.

  Returns an array of the transfer (transaction objects)."
  [iota & args]
  (js-utils/js-apply (api iota) "sendTrytes" args))


(defn send-transfer
  "Wrapper function that basically does `prepare-transfers`, as well as
  `attach-to-tangle` and finally, it broadcasts and stores the transactions
  locally.

  Arguments:
  iota - IOTA client instance
  seed - string tryte-encoded seed. If provided, will be used for signing and
         picking inputs.
  depth - int depth
  `min-weight-magnitude` - minimum weight magnitude
  transfers - coll of transfer objects:
    :address - string 81-tryte encoded address of recipient
    :value - int value to be transferred.
    :message - string tryte-encoded message to be included in the bundle.
    :tag - string 27-tryte encoded tag.
  options: Optional map with keys:
    :inputs - coll of inputs used for funding the transfer
    :address: string if defined, this address will be used for sending the
              remainder value (of the inputs) to.
  callback: fn Optional callback

  Returns a collection of the transfer (transaction objects)."
  [iota & args]
  (js-utils/js-apply (api iota) "sendTransfer" args))


(defn promote-transaction
  "Promotes a transaction by adding spam on top of it, as long as it is
  promotable. Will promote by adding transfers on top of the current one with
  `delay` interval. Use `{:interrupt bool/fn}` as params to terminate the
  promotion. If `:delay` in params is set to 0 only one promotion transfer will be
  sent.

  Arguments:
  iota - IOTA client instance
  transaction - string Transaction hash, has to be tail.
  depth - int depth
  min-weight-magnitude - int minimum weight magnitude
  transfers - coll of transfer objects:
    :address - string 81-tryte encoded address of recipient
    :value - int value to be transferred.
    :message - string tryte-encoded message to be included in the bundle.
    :tag - string 27-tryte encoded tag.
  params - Map with following keys:
    :delay - int Delay between promotion transfers
    :interrupt - boolean || fn Flag to terminate promotion, can be boolean or a
                 function returning a boolean
  callback - fn Callback function with error and result.

  Returns a coll of the Promotion transfer (transaction object)."
  [iota & args]
  (js-utils/js-apply (api iota) "promoteTransaction" args))


(defn replay-bundle
  "Takes a tail transaction hash as input, gets the bundle associated with the
  transaction and then replays the bundle by attaching it to the tangle.

  Arguments:
  transaction: String Transaction hash, has to be tail
  depth: int depth
  min-weight-magnitude: int minimun weight magnitude
  callback: fn Optional callback "
  [iota & args]
  (js-utils/js-apply (api iota) "replayBundle" args))


(defn broadcast-bundle
  "Takes a tail transaction hash as input, gets the bundle associated with the
  transaction and then rebroadcasts the entire bundle.

  Arguments:
  transaction - String Transaction hash, has to be tail
  callback - fn Optional callback"
  [iota & args]
  (js-utils/js-apply (api iota) "broadcastBundle" args))


(defn get-bundle
  "This function returns the bundle which is associated with a transaction.
  Input has to be a tail transaction (i.e. current-index = 0). If there are
  conflicting bundles (because of a replay for example) it will return multiple
  bundles. It also does important validation checking (signatures, sum, order)
  to ensure that the correct bundle is returned.

  Arguments:
  transaction - String Transaction hash, has to be tail
  callback - fn Optional callback

  Returns a collection of the corresponding bundle of a tail transaction. The
  bundle itself consists of individual transaction objects"
  [iota & args]
  (js-utils/js-apply (api iota) "getBundle" args))


(defn get-transfers
  "Returns the transfers which are associated with a seed. The transfers are
  determined by either calculating deterministically which addresses were
  already used, or by providing a list of indexes to get the addresses and the
  associated transfers from. The transfers are sorted by their timestamp. It
  should be noted that, because timestamps are not enforced in IOTA, that this
  may lead to incorrectly sorted bundles (meaning that their chronological
  ordering in the Tangle is different).

  If you want to have your transfers split into received / sent, you can use the
  utility function `iota-utils/categorize-transfers`.

  Arguments:
  seed - string tryte-encoded seed. It should be noted that this seed is not
         transferred
  options - optional map with following keys:

    :start - int Starting key index for search
    :end - int Ending key index for search
    :security - int Security level to be used for the private key / addresses,
                    which is used for getting all associated transfers.
    :inclusion-states - bool If true, it gets the inclusion states of the
                             transfers.
  callback - fn Optional callback.

  Returns an array of transfers. Each array is a bundle for the entire
  transfer."
  [iota & args]
  (js-utils/js-apply (api iota) "getBundle" args))


(defn get-account-data
  "Similar to `get-transfers`, just a bit more comprehensive in the sense that
  it also returns the addresses, transfers, inputs and balance that are
  associated and have been used with your account (seed). This function is
  useful in getting all the relevant information of your account. If you want to
  have your transfers split into received / sent, you can use the utility
  function `categorize-transfers`.

  Arguments:
  iota - IOTA client instance
  seed - Tryte-encoded seed. It should be noted that this seed is not
         transferred
  options - optional map with:
    :start - Starting key index for search
    :end - Ending key index for search
    :security - Security level to be used for the private key / addresses,
                which is used for getting all associated transfers
  callback - Optional callback with error and result

  Returns map of your account data in the following format:
  ```

  {:latest-address \"\" ; Latest, unused address which has no transactions in
                        ; the tangle
   :addresses     []    ; List of all used addresses which have transactions
                        ; associated with them
   :transfers     []    ; List of all transfers associated with the addresses
   :inputs        []    ; List of all inputs available for the seed. Follows the
                        ; `get-inputs` format of `address`, `balance`,
                        ; `security` and `key-index`
   :balance       0}    ; Latest confirmed balance
  ```"
  [iota & args]
  (js-utils/js-apply (api iota) "getAccountData" args))


(defn promotable?
  "Checks if tail transaction is promotable by calling `check-consistency` API
  call.

  Arguments:
  iota - IOTA client instance
  tail - string tail transaction hash

  Returns a core.async channel that receives true or false"
  [iota & args]
  (let [ch (chan)]
    (.then (js-utils/js-apply (api iota) "isPromotable" args)
           #(go (>! ch %)))
    ch))


(defn reattachable?
  "This API function helps you to determine whether you should replay a
  transaction or make a completely new transaction with a different seed. What
  this function does, is it takes an input address (i.e. from a spent
  transaction) as input and then checks whether any transactions with a value
  transferred are confirmed. If yes, it means that this input address has
  already been successfully used in a different transaction and as such you
  should no longer replay the transaction.

  Arguments:
  iota - IOTA client instance
  input-address: string | collection address used as input in a transaction.
                 Either string or array.
  callback: fn callback function

  Returns true or false (if you provided an array, it's an array of bools)"
  [iota & args]
  (js-utils/js-apply (api iota) "isReattachable" args))

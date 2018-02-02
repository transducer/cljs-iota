(ns cljs-iota.api
  "Core API functionality for interacting with the IOTA core.

  See https://iota.readme.io/v1.2.0/reference"
  (:require [cljs-iota.js-utils :as js-utils]))


(defn api
  "Gets API object from IOTA library instance.

  Parameter:
  IOTA - IOTA library instance"
  [iota]
  (aget iota "api"))


;;;;
;;;; Standard API

(defn get-node-info
  "Returns information about your node.

  Arguments:
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
  callback - Callback with error and result

  Return Values:

    :address - address of your peer
    :numberOfAllTransactions - Number of all transactions sent (invalid, valid,
  already-seen)
    :numberOfInvalidTransactions - Invalid transactions your peer has sent you.
  These are transactions with invalid signatures or overall schema.
    :numberOfNewTransactions - New transactions which were transmitted."
  [iota & args]
  (js-utils/js-apply (api iota) "getNeighbors" args))


(defn add-neighbors
  "Add a list of neighbors to your node. It should be noted that this is only
  temporary, and the added neighbors will be removed from your set of neighbors
  after you relaunch IRI.

  Arguments:
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
  uris - List of URI elements
  callback - Callback with error and result

  The URI (Unique Resource Identification) for adding neighbors is:
  udp://IPADDRESS:PORT"
  [iota & args]
  (js-utils/js-apply (api iota) "removeNeighbors" args))


(defn get-tips
  "Returns the list of tips.

  Arguments:
  callback - Callback with error and result"
  [iota & args]
  (js-utils/js-apply (api iota) "getTips" args))


(defn find-transactions
  "Find the transactions which match the specified input and return. All input
  values are lists, for which a list of return values (transaction hashes), in
  the same order, is returned for all individual elements.

  Arguments:
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
  "DOES NOT WORK PROPERLY - returns only nines.

  Returns the raw transaction data (trytes) of a specific transaction. These
  trytes can then be easily converted into the actual transaction object. See
  utility functions for more details.

  Arguments:
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
  trytes - List of raw data of transactions to be rebroadcast
  callback - Callback with error and result"
  [iota & args]
  (js-utils/js-apply (api iota) "broadcastTransactions" args))


(defn store-transactions
  "Store transactions into the local storage. The trytes to be used for this call
  are returned by `attach-to-tangle`.

  Arguments:
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
  hashes - Array List of transaction hashes
  callback - Function callback with error and result

  Returns list of all the inclusion states of the transaction hashes."
  [iota & args]
  (js-utils/js-apply (api iota) "getLatestInclusion" args))


;;; Mentioned in iota.lib.js README, but not implemented on API object

#_(defn broadcast-and-store
  "Wrapper function for `broadcast-transactions` and `store-transactions`.

  Arguments:
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


;;; TODO next up: https://github.com/iotaledger/iota.lib.js/#getinputs

(defn get-account-data
  "Similar to `get-transfers`, just a bit more comprehensive in the sense that
  it also returns the addresses, transfers, inputs and balance that are
  associated and have been used with your account (seed). This function is
  useful in getting all the relevant information of your account. If you want to
  have your transfers split into received / sent, you can use the utility
  function `categorize-transfers`.


  Arguments:
  seed - Tryte-encoded seed. It should be noted that this seed is not
         transferred
  options - optional map with:
    :start - Starting key index for search
    :end - Ending key index for search
  security - Security level to be used for the private key / addresses,
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

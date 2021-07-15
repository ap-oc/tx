(ns tx.core
  (:gen-class))

(require '[clojure.data.csv :as csv]
         '[clojure.java.io :as io])

(defn account
  "Writes to ledger"
  [amount debit credit currency]
  (with-open [writer (io/writer "ledger.csv" :append true)]
    (csv/write-csv writer [[(System/currentTimeMillis) amount debit credit currency]]))
  )

(defn init
  "Initializes a ledger with its headers"
  []
  (if-not (.exists (io/file "ledger.csv"))
    (account "amount" "debit" "credit" "currency")))

(defn report
  "Reports ledger status"
  [fname]
  (with-open [reader (io/reader fname)]
    (doall
     (csv/read-csv reader))))

(defn -main
  "Main"
  [& args]
  (init)
  (when (> (count args) 1)
    (apply account args)))

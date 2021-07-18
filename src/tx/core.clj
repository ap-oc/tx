(ns tx.core
  (:gen-class))

(require '[clojure.data.csv :as csv]
         '[clojure.java.io :as io])

(defn ledger-location
  
  ;; returns an absolute datfile/ledgerfile path
  []
  (clojure.string/join "/" [(System/getProperty "user.home") ".ledger.csv"]))

(defn record
  
  ;; ledger writer
  [timestamp currency amount debit credit]
  (with-open [writer (io/writer (ledger-location) :append true)]
    (csv/write-csv writer [[timestamp currency amount debit credit]])))

(defn init
  
  ;; initializes a ledger with its headers - unless exists
  []
  (if-not (.exists (io/file (ledger-location)))
    (record "timestamp" "currency" "amount" "debit" "credit")))

(defn account

  ;; variadic for uncredited records
  ([currency amount debit]
   (account currency amount debit "energy"))

  ;; basic debit & credit record
  ([currency amount debit credit]
   (record (System/currentTimeMillis) currency amount debit credit)))

(defn ledger

  ;; converts CSV into a hashmap
  [data]
  (map zipmap
       (->> (first data) (map keyword) repeat)
       (rest data)))

(defn month-old-filter

  ;; filters transactions out of past 30 days
  [tx]
  (> (read-string (get tx :timestamp))
     (- (System/currentTimeMillis) (* 1000 60 60 24 30))))

(defn report
  
  ;; reports ledger status
  []
  (with-open [reader (io/reader (ledger-location))]
    (->> (ledger (csv/read-csv reader))
         (filter month-old-filter)
         (println))
    ))

(defn -main
  
  ;; main
  [& args]
  (init)
  (when (> (count args) 1)
    (apply account args))
  (report))

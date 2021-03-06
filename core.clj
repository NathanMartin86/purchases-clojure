(ns purchases-clojure.core
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])

  (:gen-class))

(defn read-purchases []
  (let [purchases (slurp "purchases.csv")
        purchases (str/split-lines purchases)
        purchases (map (fn [line]
                         (str/split line #","))
                       purchases)
        header (first purchases)
        purchases (rest purchases)
        purchases (map (fn [line]
                         (interleave header line))
                       purchases)
        purchases (map (fn [line]
                         (apply hash-map line))
                       purchases)
        purchases (walk/keywordize-keys purchases)]
      purchases))


(defn purchases-html []
  (let [purchases (read-purchases)]
    (map (fn [line]
           [:p
            (str (:customer_id line)
                 (:date line)
                 (:credit_card line)
                 (:cvv line)
                 (:category line)
                 [:br]
                 )]))
    purchases))



(defn handler [request]
{:status 200
 :headers {"Content-type" "text/html"}
 :body (h/html [:html
                [:body
                 :a (purchases-html)]])})

(defn -main [& args]
  (j/run-jetty #'handler {:port 3000 :join? false}))

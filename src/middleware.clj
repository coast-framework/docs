(ns middleware
  (:require [coast]
            [clojure.string :as string]
            [components]))

(defn auth [handler]
  (fn [request]
    (if (some? (get-in request [:session :member/email]))
      (handler request)
      (coast/unauthorized "HAL9000 says: I'm sorry Dave, I can't let you do that"))))


(defn capitalize-words
  "Capitalize every word in a string"
  [s]
  (string/replace
    (->> (string/split (str s) #"\b")
         (map string/capitalize)
         string/join)
    #"[^a-zA-Z\d\s:]" " "))


(defn custom-layout [handler]
  (fn [request]
    (let [hiccup (handler request)
          response (-> (coast/ok hiccup :html)
                       (assoc :title (capitalize-words (get-in request [:params :doc]))))
          body (-> (components/layout request response)
                   (coast/html)
                   (str))]
      (assoc response :body body))))

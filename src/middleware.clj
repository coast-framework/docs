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


(defn layout [handler]
  (fn [request]
    (let [original-response (handler request)]
      (if (map? original-response)
        original-response
        (let [title (capitalize-words (get-in request [:params :doc]))]
          (-> (components/layout request {:body original-response
                                          :title title})
              (coast/html)
              (str)
              (coast/ok :html)))))))

(ns middleware
  (:require [coast]
            [clojure.string :as string]))

(defn auth [handler]
  (fn [request]
    (if (some? (get-in request [:session :member/email]))
      (handler request)
      (coast/unauthorized "HAL9000 says: I'm sorry Dave, I can't let you do that"))))


(defn set-current-member [handler]
  (fn [request]
    (let [email (get-in request [:session :member/email])
          m {:member/email email}
          member (coast/find-by :member m)]
      (handler (assoc request :member member)))))


(defn capitalize-words
  "Capitalize every word in a string"
  [s]
  (string/replace
    (->> (string/split (str s) #"\b")
         (map string/capitalize)
         string/join)
    #"[^a-zA-Z\d\s:]" " "))


(defn set-title [handler]
  (fn [request]
    (-> (assoc request :title (capitalize-words (get-in request [:params :doc])))
        (handler))))

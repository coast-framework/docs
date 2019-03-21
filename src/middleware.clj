(ns middleware
  (:require [coast]))

(defn auth [handler]
  (fn [request]
    (if (some? (get-in request [:session :member/email]))
      (handler request)
      (coast/unauthorized "HAL9000 says: I'm sorry Dave, I can't let you do that"))))

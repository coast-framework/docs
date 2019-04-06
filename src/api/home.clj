(ns api.home
  (:require [coast]
            [post]
            [utils]))

(defn index [request]
  {:status "ok"})

(defn save-release [body]
  (let [r-name (get-in body ["release" "name"])
        r-body (get-in body ["release" "body"])
        member (coast/find-by :member {:email "bot@coastonclojure.com"})
        [_ error] (-> (coast/insert {:post/title r-name
                                     :post/body r-body
                                     :post/slug (post/slug r-name)
                                     :post/member (:member/id member)
                                     :post/published-at (coast/now)})
                      (coast/rescue))]
    (if (some? error)
      (do
        (coast/server-error error :json))
        ;(slack/send-errors (str "Couldn't receive github release " error)))
      (coast/ok {:status "success"} :json))))

(defn release [{:keys [raw-body body headers]}]
  (let [x-hub-sig (get headers "x-hub-signature")
        server-sig (utils/hmac (coast/env :hook-secret) raw-body)
        x-hub-event (get headers "x-github-event")]
    (if (= x-hub-sig server-sig)
      (condp = x-hub-event
        "ping" (coast/ok body :json)
        "release" (save-release body))
      (coast/server-error {:status "error" :reason "signatures don't match"} :json))))

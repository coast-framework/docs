(ns server-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [server :refer [app]]
            [post]
            [coast]
            [coast.migrations]
            [coast.db]))

(defn migrate-fixture [f]
        (coast.migrations/migrate)
        (f)
        (coast.db/-main "drop"))

(use-fixtures :once migrate-fixture)

(deftest home-test
  (testing "home route"
    (let [response (app {:uri "/" :request-method :get})]
      (is (= (:status response) 200)))))

(deftest not-found-test
  (testing "not-found route"
    (let [response (app {:uri "/not-found" :request-method :get})]
      (is (= (:status response) 404)))))

(deftest post-test
  (let [slug (post/slug "hello")
        _ (coast/insert {:member/id 1
                         :member/email "hello"
                         :member/password "hello"})
        post (coast/insert {:post/title "hello"
                            :post/body "hello"
                            :post/slug slug
                            :post/member 1})]
    (testing "post/view unpublished post"
      (let [response (app {:uri (coast/url-for :post/view post)
                           :request-method :get})]
        (is (= (:status response) 404))))))

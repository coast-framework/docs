(ns migrations.20190323113040-create-table-post
  (:require [coast.db.migrations :refer :all]))

(defn change []
  (create-table :post
    (references :member)
    (text :title)
    (text :body)
    (integer :published-at)
    (timestamps)))

(ns migrations.20190328101840-add-slug-to-post
  (:require [coast.db.migrations :refer :all]))

(defn change []
  (add-column :post :slug :text))

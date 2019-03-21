(ns docs
  (:require [markdown.core :as markdown]
            [clojure.string :as string]))


(defn heading-anchors [text state]
  (if (true? (:inline-heading state))
    (let [[_ heading] (re-find #">(.*)<" text)
          anchor (-> (string/replace heading #"\s" "-")
                     (string/lower-case))
          anchor (str "<h$1 id=\"user-content-" anchor "\">")]
      [(string/replace text #"<h([0-9])>" anchor) state])
    [text state]))


(defn tip [text state]
  (if (string/starts-with? text "<p>TIP:")
    (let [s (string/replace text #"<p>" "<p class=\"tip\">")]
      [s state])
    [text state]))


(defn note [text state]
  (if (string/starts-with? text "<p>NOTE:")
    (let [s (string/replace text #"<p>" "<p class=\"note\">")]
      [s state])
    [text state]))


(defn -main [& args]
  (let [directory (clojure.java.io/file "docs")
        files (->> (file-seq directory)
                   (take 50)
                   (filter #(not (.isDirectory %)))
                   (map #(.getName %)))]
    (doseq [filename files]
     (spit
      (str "html/" filename)
      (markdown/md-to-html-string (slurp (str "docs/" filename))
                                  :custom-transformers [heading-anchors tip note])))))

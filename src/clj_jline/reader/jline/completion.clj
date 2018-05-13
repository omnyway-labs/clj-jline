(ns clj-jline.reader.jline.completion
  (:require [clj-jline.completion :as completion])
  (:import [jline.console.completer Completer]))

(defn get-prefix [buffer cursor]
  (let [buffer (or buffer "")]
    (or (completion/get-word-ending-at buffer cursor) "")))

(defn make-completer [eval-fn redraw-line-fn ns]
  (proxy [Completer] []
    (complete [^String buffer cursor ^java.util.List candidates]
      (let [prefix ^String (get-prefix buffer cursor)
            prefix-length (.length prefix)]
        (if (zero? prefix-length)
          -1
          (let [possible-completions (eval-fn prefix ns)]
            (if (empty? possible-completions)
              -1
              (do
                (.addAll candidates (map str possible-completions))
                (redraw-line-fn)
                (- cursor prefix-length)))))))))

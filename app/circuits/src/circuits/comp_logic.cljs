(ns circuits.comp-logic)

;; exp function pasted from John Lawrence Aspden on stackoverflow
(defn exp  [x n]
  (loop  [acc 1 n n]
    (if  (zero? n) acc
      (recur  (* x acc)  (dec n)))))

(defn decode  [input] 
  (->> input 
       reverse
       (map-indexed  (fn  [k v]  (if  (true? v)  (exp 2 k) nil)))
       (remove nil?) 
       (reduce +)))  
(defn get-n-tuples [coll]
  (apply map vector coll))
(defn only-one? [coll] (= (count coll) 1))
(defn all-true? [coll] (every? true? coll))
(defn do-mux  [data control]
  (nth data  (decode control))) 
(defn do-and  [args]
  (let [n-tuples (get-n-tuples args)
        all-true (map all-true? n-tuples)]
    (vec all-true))) 
(defn do-or [args]
  (let [n-tuples (get-n-tuples args)
        any-true-nils (map #(some true? %) n-tuples)
        any-true (map (fn [x] (if (nil? x) false x)) any-true-nils)]
    (vec any-true)))
(get-n-tuples [[1 2 3] [1 2 3] [1 2 3]])
;; xor - true iff a single input is on
(defn do-xor [args]
  (let [n-tuples (get-n-tuples args)
        filtered-true (map #(filter true? %) n-tuples)
        results (map only-one? filtered-true)]
    (vec results)))
(defn do-decode [arg]
  (vec (map vec (partition 1 arg))))

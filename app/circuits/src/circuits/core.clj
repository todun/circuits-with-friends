(ns circuits.core
  (:require [circuits.validate :as valid]
            [circuits.comp-logic :as logic]
            [circuits.comp-builder :as build]))
(def circuit  (atom {}))

(defn set-state  [component-map]
  (reset! circuit component-map))

(defn clear-state [] (reset! circuit {}))
(defn generate-id [species circuit]
  (let [component-count (count circuit)]
    (inc component-count))) 

(declare function-map gen-inputs)

(defn evaluate-component  [component]
  (let  [component-type  (component :species)
         eval-fn  (function-map component-type)]
    (eval-fn component)))

(defn evaluate  [id state]
  (if (valid/validate-state state)
    (let [newstate  (set-state state)
          result  (evaluate-component (@circuit id))
          ret-val {:result result :state @circuit}
          cleared-state  (clear-state)
          ]
      ret-val)
    nil))

(defn add-component [species circuit display]
  (let [new-id (generate-id species circuit)
        new-component (build/build-component species new-id)
        with-display (assoc new-component :display display)
        new-state (assoc circuit new-id with-display)
        ]
    new-state))


;; src = {:id, :field}
;; dst = {:id, :field, index}
(defn add-connection [src dst circuit]
  (let [dst-component (circuit (dst :id))
        dst-inputs (dst-component :inputs)
        dst-field (dst-inputs (dst :field)) 
        dst-vector (dst-field :connections)
        dst-index (dst :index)
        new-connection {:source-id (src :id)
                        :source-field (src :field)}
        new-invec (assoc dst-vector dst-index new-connection)
        new-circuit (assoc-in circuit [
                                       (dst :id) 
                                       :inputs 
                                       (dst :field) 
                                       :connections] 
                              new-invec)]
    new-circuit))

(defn remove-connection [src dst circuit]
  (let [dst-component (circuit (dst :id))
        dst-inputs (dst-component :inputs)
        dst-field (dst-inputs (dst :field)) 
        dst-vector (dst-field :connections)
        dst-index (dst :index)
        new-invec (assoc dst-vector dst-index {})
        new-circuit (assoc-in circuit [
                                       (dst :id) 
                                       :inputs 
                                       (dst :field) 
                                       :connections] 
                              new-invec)]
    new-circuit))

;; External Interface Functions
(defn add-component-js [species circuit display]
  (clj->js (add-component species (js->clj circuit) (js->clj display))))
(defn add-connection-js [src dst circuit]
  (clj->js (add-connection (js->clj src) (js->clj dst) (js->clj circuit)))) 
(defn remove-connection-js [src dst circuit]
  )
;; End External Interface FNs

(defn inner-fn  [mapping]
  (let  [state @circuit
         source-component  (state  (mapping :source-id))
         eval-fn  (function-map  (source-component :species))
         the-outputs  (eval-fn source-component)
         result (the-outputs (mapping :source-field))]
    result))
(defn gen-input-field  [kvpair]
  {(key kvpair)  (map inner-fn  ((val kvpair) :connections))})
(defn gen-inputs  [component]
  (let  [input-maps  (component :inputs)
         input-fields  (map gen-input-field input-maps)
         result (if (> (count input-fields) 1) 
                  (apply conj input-fields) 
                  (first input-fields))]
    result))
(> 1 (count [1 2]))
(count [1 2])
; for collections with only 1 value
;; Eval functions for every component

(defn and-eval  [andgate]
  (let [inputs (gen-inputs andgate)] 
    {:q (logic/do-and inputs)}))
(defn nand-eval [nandgate]
  (let [and-value (and-eval nandgate)
        negated (not and-value)]
    {:q negated}))
(defn or-eval [orgate]
  (let [inputs (gen-inputs orgate)] 
    (logic/do-or inputs)))
(defn nor-eval [norgate]
  (let [or-value (or-eval norgate)
        negated (not or-value)]
    {:q negated})) 

(defn xor-eval [xorgate]
  (let [inputs (gen-inputs xorgate)]
    {:q (logic/do-xor inputs)}))
(defn xnor-eval [xnorgate]
  (let [xor-value (xor-eval xnorgate)
        negated (not xor-value)]
    {:q negated}))

(defn decoder-eval [decoder]
  (let [input (gen-inputs decoder)]
    {:q (logic/do-decode input)}))

(defn mux-eval  [mux]
  (let  [inputs  (gen-inputs mux)
         data  (inputs :data)
         control  (flatten  (inputs :control))
         result (logic/do-mux data control)]
    {:q result})) 
(defn not-eval [notgate]
  (let [inputs (gen-inputs notgate)
        data (:data inputs)
        only-one (first data)]
    {:q (vec (map not only-one))}))

;; Memory Components
(defn register-eval [register]
  (let [state (register :state)
        data (state :data)
        inputs (gen-inputs register)
        enabled (inputs :enable)]
    (if enabled
      (let [updated-state (assoc state :data (first (inputs :data)))
            updated-register (assoc register :state updated-state)]
        (swap! circuit assoc (register :id) updated-register)))
    {:q data}))
(defn d-flipflop-eval [dff]
  (let [state (dff :state)
        data (state :data)
        inputs (gen-inputs dff)
        enabled (inputs :enable)]
    (if enabled
      (let [updated-state (assoc state :data (vec (first (inputs :data))))
            updated-dff (assoc dff :state updated-state)]
        (swap! circuit assoc (dff :id) updated-dff)))
    {:q data :q-bar (vec (map not data))}))
(defn t-flipflop-eval [tff]
  (let [state (tff :state)
        data (state :data)
        inputs (gen-inputs tff)
        enabled (inputs :enable)]
    (if enabled
      (let [updated-state (assoc state :data (vec (map not data)))
            updated-dff (assoc tff :state updated-state)]
        (swap! circuit assoc (tff :id) updated-dff)))
    {:q data :q-bar (vec (map not data))}))
(defn inputpin-eval  [inputpin]
  (let  [state  (inputpin :state)
         data  (state :data)]
    {:q data}))
(defn outputpin-eval [outputpin]
  (let [inputs (gen-inputs outputpin)]
    inputs))

(def function-map  {"notgate" not-eval
                    "andgate" and-eval 
                    "nandgate" nand-eval
                    "orgate" or-eval
                    "norgate" nor-eval
                    "xorgate" xor-eval
                    "xnorgate" xnor-eval
                    "decoder" decoder-eval  
                    "mux" mux-eval 
                    "register" register-eval
                    "dflipflop" d-flipflop-eval
                    "tflipflop" t-flipflop-eval
                    "inputpin" inputpin-eval
                    })

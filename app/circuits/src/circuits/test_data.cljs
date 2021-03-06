(ns circuits.test-data
  (:require [circuits.comp-builder :as build]))

(def default-display {:x 30 :y 30 :size 70})
(def myandgate  {:id :and0 :label "PcPlus4"
                 :species "andgate"
                 :state  {}
                 :inputs  {:data  {
                                   :numPins 2
                                   :wordLength 1
                                   :connections  [
                                                          {:source-id :ip0
                                                           :source-field :q}
                                                          {:source-id :ip1
                                                           :source-field :q}]}}
                 :outputs  {
                            :q  {:numPins 1
                                 :wordLength 1}}
                 :display default-display})

(def myorgate  {:id :or0 :label "PcPlus4"
                 :species "orgate"
                 :state  {}
                 :inputs  {:data  {
                                   :numPins 2
                                   :wordLength 1
                                   :connections  [
                                                          {:source-id :ip0
                                                           :source-field :q}
                                                          {:source-id :ip1
                                                           :source-field :q}]}}
                 :outputs  {
                            :q  {:numPins 1
                                 :wordLength 1}}
                 :display default-display})

(def mymux  {:id :mux0 :label "RegWrite"
             :species "mux"
             :state  {}
             :inputs  {:data  {
                               :numPins 4
                               :wordLength 1
                               :connections  [
                                                      {:source-id :ip0
                                                       :source-field :q}
                                                      {:source-id :ip1
                                                       :source-field :q}
                                                      {:source-id :ip2
                                                       :source-field :q}
                                                      {:source-id :ip3
                                                       :source-field :q}
                                                      ]}
                       :control  {
                                  :numPins 1
                                  :wordLength 2
                                  :connections  [
                                                         {:source-id :ip4
                                                          :source-field :q}]}}
             :outputs  {
                        :q  {:numPins 1
                             :wordLength 1}}})
(def myfatmux  {:id :mux1 :label "RegWrite"
                :species "mux"
                :state  {}
                :inputs  {:data  {
                                  :numPins 4
                                  :wordLength 8
                                  :connections  [
                                                         {:source-id :ip5
                                                          :source-field :q}
                                                         {:source-id :ip6
                                                          :source-field :q}
                                                         {:source-id :ip7
                                                          :source-field :q}
                                                         {:source-id :ip8
                                                          :source-field :q}
                                                         ]}
                          :control  {
                                     :numPins 1
                                     :wordLength 2
                                     :connections  [
                                                            {:source-id :ip9
                                                             :source-field :q}]}}
                :outputs  {
                           :q  {:numPins 1
                                :wordLength 8}}})



(def input-pin-0  {:id :ip0 :label "teh 1337 p1n"
                   :species "inputpin"
                   :display default-display
                   :state  {:data  [true]}
                   :inputs  {}
                   :outputs  {:q  {
                                   :numPins 1
                                   :wordLength 1}}})
(def input-pin-1  (assoc input-pin-0 :id :ip1))
(def input-pin-2  (assoc input-pin-0 :id :ip2
                         :state  {:data  [false]}))
(def input-pin-3  (assoc input-pin-0 :id :ip3))
(def input-pin-4  {:id :ip4 :label "asdf"
                   :species "inputpin"
                   :state  {:data  [true false]}
                   :inputs  {}
                   :outputs  {:q  {
                                   :numPins 1
                                   :wordLength 2}}
                   })
(def input-pin-5  {:id :ip5
                   :species "inputpin"
                   :state  {:data  [true false true false
                                    true false true false]}
                   :inputs  {}
                   :outputs  {:q  {
                                   :numPins 1
                                   :wordLength 8}}
                   })
(def input-pin-6  (assoc input-pin-5
                         :id :ip6
                         :state  {:data  [false false false false
                                          false false false false]}))
(def input-pin-7  (assoc input-pin-5
                         :ip :ip7
                         :state  {:data  [false false false false
                                          false false false false]}))
(def input-pin-8  (assoc input-pin-5
                         :ip :ip8
                         :state  {:data  (vec  (repeat 8 true))}))
(def input-pin-9  (assoc input-pin-5
                         :ip :ip9
                         :state  {:data  [true true]}
                         :outputs  {:q  {
                                         :numPins 1
                                         :wordLength 2}}))

(def register {:id :reg0 :label "pc"
               :species "register"
               :state {:data [true true
                              false false
                              true true
                              false false]}
               :inputs {:enable {:num-pins 1
                                 :word-length 1
                                 :connections [{:source-id :ip42
                                                        :source-field :q}]}
                        :data {:num-pins 1
                               :word-length 8
                               :connections [{:source-id :ip14
                                                      :source-field :q}]}}
               :outputs {:data {:num-pins 1
                                :word-length 8}}})
(def eight-bit-pin (assoc input-pin-0
                          :id :ip14
                          :state {:data [false true false true
                                         false true false true]}
                          :inputs {}
                          :outputs {:q {:num-pins 1
                                        :word-length 8}}))
(def register-enable-pin  {:id :ip42 :label "reg enable"
                           :species "inputpin"
                           :state  {:data [true]}
                           :inputs  {}
                           :outputs  {:q  {
                                           :numPins 1
                                           :wordLength 1}}})

(def d-flip-flop {:id :dff0 :label "Tick Tock goes the Flip-Flop"
                  :species "dflipflop"
                  :state {:data [false]}
                  :inputs {:data {:num-pins 1
                                  :word-length 1
                                  :connections [{:source-id :ip0
                                                         :source-field :q}]}
                           :enable {:num-pins 1
                                    :word-length 1
                                    :connections [{:source-id :ip42
                                                           :source-field :q}]}}
                  :outputs {:q {:num-pins 1
                                :word-length 1}
                            :q-bar {:num-pins 1
                                    :word-length 1}}})
(def t-flip-flop (assoc d-flip-flop
                        :id :tff0
                        :species "tflipflop"))
(def not-gate {:id :not0 :label "NO SOUP 4 U"
               :species "notgate"
               :state {}
               :inputs {:data {:num-pins 1
                               :word-length 1
                               :connections [{:source-id :ip0
                                                      :source-field :q}]}}
               :outputs {:data {:num-pins 1
                                :word-length 1}}})

(def components  {:ip0 input-pin-0 :ip1 input-pin-1
                  :ip2 input-pin-2 :ip3 input-pin-3
                  :ip4 input-pin-4 :ip5 input-pin-5
                  :ip6 input-pin-6 :ip7 input-pin-7
                  :ip8 input-pin-8 :ip9 input-pin-9
                  :and0 myandgate :mux0 mymux
                  :mux1 myfatmux :reg0 register
                  :ip42 register-enable-pin
                  :dff0 d-flip-flop
                  :tff0 t-flip-flop
                  :ip14 eight-bit-pin
                  :not0 not-gate})
(def t1-outputpin {:id 3 :species "outputpin"
                   :inputs {:data {:word-length 1
                                  :num-pins 1
                                  :connections [{:source-id :and0
                                                 :source-field :q}]}}
                   :outputs {:q {:word-length 1
                                :num-pins 1}}
                   :display default-display})
(def and-set {:ip0 input-pin-0 :ip1 input-pin-1
             :and0 myandgate 3 t1-outputpin})
(def or-set {:ip0 input-pin-0 :ip1 input-pin-1
             :or0 myorgate 3 t1-outputpin})

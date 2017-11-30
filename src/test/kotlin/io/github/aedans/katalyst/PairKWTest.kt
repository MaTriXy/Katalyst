package io.github.aedans.katalyst

import kategory.*
import kategory.laws.EqLaws

class PairKWTest : UnitSpec() {
    init {
        testLaws(EqLaws.laws { PairKW.pure(it) })
        testLaws(FunctorLaws.laws(PairKW.functor(), { PairKW.pure(it) }, Eq.any()))
        testLaws(ApplicativeLaws.laws(PairKW.applicative(), Eq.any()))
        testLaws(MonadLaws.laws(PairKW.monad(), Eq.any()))
        testLaws(FoldableLaws.laws(PairKW.foldable(), { PairKW.pure(it) }, Eq.any()))
        testLaws(TraverseLaws.laws(PairKW.traverse(), PairKW.functor(), { PairKW.pure(it) }, Eq.any()))
    }
}

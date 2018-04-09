package io.github.aedans.katalyst

import arrow.Kind
import arrow.core.Eval
import arrow.typeclasses.Functor

fun <F, A> Algebra(it: Algebra<F, A>) = it

fun <F, A> Coalgebra(it: Coalgebra<F, A>) = it

/**
 * Fold over a kind.
 */
typealias Algebra<F, A> = (Kind<F, A>) -> A

/**
 * Unfold over a kind.
 */
typealias Coalgebra<F, A> = (A) -> Kind<F, A>

/**
 * The composition of cata and ana.
 */
fun <F, A, B> hylo(
        a: A,
        alg: Algebra<F, Eval<B>>,
        coalg: Coalgebra<F, A>,
        FF: Functor<F>
): B = FF.run {
    fun h(a: A): Eval<B> = alg(coalg(a).map { Eval.defer { h(it) } })
    return h(a).value()
}

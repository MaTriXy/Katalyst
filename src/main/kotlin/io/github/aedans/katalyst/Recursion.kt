package io.github.aedans.katalyst

import arrow.HK
import arrow.free.*
import arrow.instances.ComposedFunctor
import arrow.typeclasses.*
import io.github.aedans.katalyst.syntax.*

/**
 * The composition of cata and ana.
 */
fun <F, A, B> hylo(
        a: A,
        alg: Algebra<F, B>,
        coalg: Coalgebra<F, A>,
        FF: Functor<F>
): B = alg(FF.map(coalg(a)) { hylo(it, alg, coalg, FF) })

/**
 * Hylo generalized over a monad.
 */
fun <M, F, A, B> hyloM(
        a: A,
        algM: AlgebraM<M, F, B>,
        coalgM: CoalgebraM<M, F, A>,
        TF: Traverse<F>,
        MM: Monad<M>
): HK<M, B> = hylo(
        a,
        { MM.flatMap(it.unnest()) { MM.flatMap(TF.sequence(MM, it), algM) } },
        { coalgM(it).nest() },
        ComposedFunctor(MM, TF)
)

/**
 * Hylo generalized over a comonad.
 */
fun <W, N, F, A, B> ghylo(
        a: A,
        dFW: DistributiveLaw<F, W>,
        dNF: DistributiveLaw<N, F>,
        gAlg: GAlgebra<W, F, B>,
        gCoalg: GCoalgebra<N, F, A>,
        CW: Comonad<W>,
        MN: Monad<N>,
        FF: Functor<F>
): B = hylo<YonedaKindPartial<F>, HK<N, A>, HK<W, B>>(
        MN.pure(a),
        { CW.map(dFW.invoke(it.ev().map(CW::duplicate).lower()), gAlg) },
        { Yoneda.apply(FF.map(dNF.invoke(MN.map(it, gCoalg)), MN::flatten), FF) },
        Yoneda.functor()
).let(CW::extract)

/**
 * Hylo generalized over a monad and a comonad.
 */
fun <W, N, M, F, A, B> ghyloM(
        a: A,
        dFW: DistributiveLaw<F, W>,
        dNF: DistributiveLaw<N, F>,
        gAlgM: GAlgebraM<W, M, F, B>,
        gCoalgM: GCoalgebraM<N, M, F, A>,
        CW: Comonad<W>,
        TW: Traverse<W>,
        MN: Monad<N>,
        TN: Traverse<N>,
        MM: Monad<M>,
        TF: Traverse<F>
): HK<M, B> = hyloM<M, F, HK<N, A>, HK<W, B>>(
        MN.pure(a),
        { TW.traverse(dFW.invoke(TF.map(it, CW::duplicate)), gAlgM, MM) },
        { MM.map(TN.traverse(it, gCoalgM, MM)) { TF.map(dNF.invoke(it), MN::flatten) } },
        TF,
        MM
).let { MM.map(it, CW::extract) }

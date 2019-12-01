package cadenza.semantics

import cadenza.jit.Code
import cadenza.*
import com.oracle.truffle.api.frame.FrameDescriptor

typealias Ctx = Env<Type>

// terms can be checked and inferred. The result is an expression.
abstract class Term {
  @Throws(TypeError::class) open fun check(ctx: Ctx, expectedType: Type): Witness = infer(ctx).match(expectedType)
  @Throws(TypeError::class) abstract fun infer(ctx: Ctx): Witness

  // provides an expression with a given type in a given frame
  abstract class Witness internal constructor(val type: Type) {
    abstract fun compile(fd: FrameDescriptor): Code
    @Throws(TypeError::class)
    fun match(expectedType: Type): Witness =
      if (type == expectedType) this
      else throw TypeError("type mismatch", type, expectedType)
  }

  companion object {
    @Suppress("unused")
    fun tvar(name: String, loc: Loc? = null): Term = object : Term() {
      @Throws(TypeError::class)
      override fun infer(ctx: Ctx): Witness = object : Witness(ctx.lookup(name)) {
        override fun compile(fd: FrameDescriptor): Code = Code.`var`(fd.findOrAddFrameSlot(name), loc)
      }
    }

    @Suppress("unused")
    fun tif(cond: Term, thenTerm: Term, elseTerm: Term, loc: Loc? = null): Term = object : Term() {
      @Throws(TypeError::class)
      override fun infer(ctx: Ctx): Witness {
        val condWitness = cond.check(ctx, Type.Bool)
        val thenWitness = thenTerm.infer(ctx)
        val actualType = thenWitness.type
        val elseWitness = elseTerm.check(ctx, actualType)
        return object : Witness(actualType) {
          override fun compile(fd: FrameDescriptor): Code {
            return Code.If(actualType, condWitness.compile(fd), thenWitness.compile(fd), elseWitness.compile(fd), loc)
          }
        }
      }
    }

    @Suppress("unused")
    fun tapp(trator: Term, vararg trands: Term, loc: Loc? = null): Term = object : Term() {
      @Throws(TypeError::class)
      override fun infer(ctx: Ctx): Witness {
        val wrator = trator.infer(ctx)
        var currentType = wrator.type
        val wrands = trands.map {
          val arr = currentType as Type.Arr? ?: throw TypeError("not a fun type")
          val out = it.check(ctx, arr.argument)
          currentType = arr.result
          return out
        }.toTypedArray<Witness>()
        return object : Witness(currentType) {
          override fun compile(fd: FrameDescriptor): Code {
            return Code.App(
              wrator.compile(fd),
              wrands.map { it.compile(fd) }.toTypedArray(),
              loc
            )
          }
        }
      }
    }

    @Suppress("UNUSED_PARAMETER","unused")
    fun tlam(names: Array<Pair<Name,Type>>, body: Term, loc: Loc? = null): Term = object : Term() {
      override fun infer(ctx: Ctx): Witness {
        todo
      }
    }
  }
}
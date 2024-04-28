import scala.meta._

object TreeTransformer {
  def transform(str: String): String = {
    implicit val dialect: Dialect = dialects.Scala3
    val tree = str.parse[Source].get
    val tree1 = removeInlineTransformer(tree)
    tree1.syntax
  }

  private val removeInlineTransformer: Transformer = new Transformer {
    private def removeInlineMod(mods: List[Mod]): List[Mod] = mods.filter(_.isNot[Mod.Inline])

    override def apply(tree: Tree): Tree = {
      def removeInline(
                        mods: List[Mod],
                        ename: Term.Name,
                        tparams: Type.ParamClause,
                        paramss: List[Term.ParamClause],
                        tpeopt: Option[Type],
                        expr: Term,
                      ): Tree = {
        val mods1 = removeInlineMod(mods)

        val paramss1 = paramss.map(_.map {
          case param"..$paramMods $name: $paramTpeopt = $expropt" =>
            val paramMods1 = removeInlineMod(paramMods)
            param"..$paramMods1 $name: $paramTpeopt = $expropt"
        })

        q"..$mods1 def $ename[..$tparams](...$paramss1): $tpeopt = $expr"
      }

      def ignoreMacros(
                       mods: List[Mod],
                       ename: Term.Name,
                       tparams: Type.ParamClause,
                       paramss: List[Term.ParamClause],
                       tpeopt: Option[Type],
                       expr: Term,
                     ): Tree = {
        expr match {
          case _: Term.SplicedMacroExpr => tree
          case _ => removeInline(mods, ename, tparams, paramss, tpeopt, expr)
        }
      }

      val tree1 = tree match {
        case q"..$mods def $ename: $tpeopt = $expr" =>
          ignoreMacros(mods, ename, Nil, Nil, tpeopt, expr)
        case q"..$mods def $ename[..$tparams](...$paramss): $tpeopt = $expr" =>
          ignoreMacros(mods, ename, tparams, paramss, tpeopt, expr)
        case _ => tree
      }

      super.apply(tree1)
    }
  }
}

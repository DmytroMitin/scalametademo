import scala.meta._

object TreeTransformer {
  def transform(str: String): String = {
    implicit val dialect: Dialect = dialects.Scala3
    val tree = str.parse[Source].get
    val tree1 = removeInlineTransformer(tree)
    tree1.syntax
  }

  private val removeInlineTransformer: Transformer = new Transformer {
    private def removeInline(mods: List[Mod]): List[Mod] = mods.filter(_.isNot[Mod.Inline])

    private def handleMatch(
                             mods: List[Mod],
                             ename: Term.Name,
                             tparams: Type.ParamClause,
                             paramss: List[Term.ParamClause],
                             tpeopt: Option[Type],
                             expr: Term,
                           ): Tree = {
      val mods1 = removeInline(mods)

      val paramss1 = paramss.map(_.map {
        case param"..$paramMods $name: $paramTpeopt = $expropt" =>
          val paramMods1 = removeInline(paramMods)
          param"..$paramMods1 $name: $paramTpeopt = $expropt"
      })

      q"..$mods1 def $ename[..$tparams](...$paramss1): $tpeopt = $expr"
    }

    override def apply(tree: Tree): Tree = {
      val tree1 = tree match {
        case q"..$mods def $ename: $tpeopt = $expr" =>
          handleMatch(mods, ename, Nil, Nil, tpeopt, expr)
        case q"..$mods def $ename[..$tparams](...$paramss): $tpeopt = $expr" =>
          handleMatch(mods, ename, tparams, paramss, tpeopt, expr)
        case _ => tree
      }

      super.apply(tree1)
    }
  }
}

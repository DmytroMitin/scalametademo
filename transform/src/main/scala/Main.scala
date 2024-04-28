import org.eclipse.jgit.api.Git

object Main extends App {
  private val root = os.pwd
  private val before = root / "before"
  private val after = root / "after"
  private val javaProjName = "javaProj"
  private val javaProj = root / javaProjName
  private val javaProjLib = javaProj / "lib"
  private val jarName = "scalachess_3-16.0.5.jar"

  println("cleaning...")

  os.remove.all(before)
  os.remove.all(after)
  os.remove.all(javaProjLib)
  os.proc("sbt", s"$javaProjName/clean").call(cwd = root)

  println("cloning...")

  Git.cloneRepository()
    .setURI("https://github.com/lichess-org/scalachess.git")
    .setDirectory(before.toIO)
    .call()

  println("transforming (removing inline)...")

  FileTransformer.transform(before = before, after = after)

  println("packaging...")

  os.proc("sbt", "scalachess/package").call(cwd = after)

  println("copying...")

  os.copy(
    from = after / "core" / "target" / "scala-3.4.1" / jarName,
    to = javaProjLib / jarName,
    createFolders = true,
  )

  println("compiling ...")

  os.proc("sbt", s"$javaProjName/compile").call(cwd = root)

  println("done")
}

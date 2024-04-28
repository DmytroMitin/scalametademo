object FileTransformer {
  def transform(before: os.Path, after: os.Path): Unit = {
    os.walk.attrs(
        before,
        skip = (path, _) => path.segments.contains("target")
      )
      .filter { case (_, info) => info.isFile }
      .foreach { case (path, _) =>
        val path1 = after / path.relativeTo(before)

        if (path.last.endsWith(".scala")) {
          val str = os.read(path)
          val str1 = TreeTransformer.transform(str)
          os.write.over(path1, str1, createFolders = true)
        } else os.copy.over(from = path, to = path1, createFolders = true)
      }
  }
}

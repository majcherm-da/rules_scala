package io.bazel.rules_scala.tut_support

import io.bazel.rulesscala.io_utils.DeleteRecursively
import java.io.File
import java.nio.file.{ Files, Paths }
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import tut.TutMain

object TutCompiler {
  def main(args: Array[String]): Unit = {
    val mdOutput = Files.createTempDirectory("tut")
    val outfile = args(1)
    val classpath = System.getProperty("java.class.path")
    TutMain.main(Array(args(0), mdOutput.toString, ".*\\.md$", "-classpath", classpath))
    // Now move the single md file in that directory onto outfile
    mdOutput.toFile.listFiles.toList match {
      case List(path) =>
        try {
          Files.copy(path.toPath, Paths.get(outfile), REPLACE_EXISTING)
          DeleteRecursively.run(mdOutput)
          println(s"wrote: $outfile")
        }
        catch {
          case t: Throwable =>
            System.err.println(s"could not move $path to $outfile. $t")
            System.exit(1)
        }
      case many =>
          System.err.println(s"expected one file in $mdOutput, found: $many")
          System.exit(1)
    }
  }
}


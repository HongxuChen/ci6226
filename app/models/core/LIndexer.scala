package models.core

import java.nio.file.{Files, Paths}

import models.utility.Config
import models.xml.Publication
import org.apache.lucene.document.{Document, Field, FieldType}
import org.apache.lucene.index.{IndexOptions, IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.FSDirectory
import play.api.Logger

import scala.sys.process.Process

object LIndex {
  val ft1 = new FieldType()
  ft1.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS)
  ft1.setTokenized(true)
  ft1.setStored(true)
  ft1.setStoreTermVectors(true)
  ft1.freeze()
}

class LIndexer(val writer: IndexWriter) {

  import LIndex._

  def writeBack() = writer.close()


  private def addDocText(key: String, value: String, document: Document) = {
    val field = new Field(key, value, ft1)
    document.add(field)
  }

  def index(pub: Publication): Unit = {
    if (pub.paperId.startsWith(Config.DBLPNOTE)) {
      Logger.warn("dblpnote entry, ignoring")
      return
    }
    pub.validate()
    Logger.debug(s"=> $pub")
    val document = new Document()

    //        is the form of "xxx/xxx/xxx", use TextField
    addDocText("paperId", pub.paperId, document)
    //        TextField
    addDocText("title", pub.title, document)
    //        StringField
    addDocText("kind", pub.kind, document)
    //        ???
    addDocText("venue", pub.venue, document)
    //        StringField
    addDocText("pubYear", pub.pubYear, document)
    //        TextField (how to join/split author list ???)
    val authorString = pub.authors.mkString(Config.splitString)
    addDocText("authors", authorString, document)
    //    pub.authors.foreach(author => addDocText("authors", author, document))

    // for free text search
    addDocText("ALL", pub.combinedString(), document)

    if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
      writer.addDocument(document)
    }
  }

}

object LIndexer {

  def apply(option: LOption, indexFolderString: String): LIndexer = {
    val analyzerWrapper = {
      //      val analyzer = new LAnalyzer(option, null)
      //      val listAnalyzer = new LAnalyzer(option, Config.splitString)
      //      val analyzerMap = Map(Config.I_AUTHORS -> listAnalyzer)
      //      new PerFieldAnalyzerWrapper(analyzer, analyzerMap)
      new LAnalyzer(option, null)
    }
    val iwc = new IndexWriterConfig(analyzerWrapper)
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE)

    val dir = {
      Logger.info(s"indexing folder: $indexFolderString")
      val indexFolder = Paths.get(indexFolderString)
      if (Files.exists(indexFolder)) {
        Logger.info("indexing folder already exists, delete")
        Process(s"rm -rf $indexFolderString").!
      }
      FSDirectory.open(indexFolder)
    }

    val writer = new IndexWriter(dir, iwc)
    new LIndexer(writer)
  }
}

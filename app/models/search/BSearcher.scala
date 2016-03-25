package models.search

import models.common.{Config, LOption}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search._
import play.api.Logger
import play.api.libs.json._

import scala.collection.JavaConversions._


case class BSearchPub(docID: Int, score: Double, info: Map[String, String])

class BSearchResult(stats: SearchStats, queryString:String, lOption: Option[LOption], val pubs: Array[BSearchPub]) {
  def toJson(): JsValue = {
    val lOptionJson = {
      lOption match {
        case Some(l) => l.toJson()
        case None => JsNull
      }
    }
    JsObject(Seq(
      "stats" -> stats.toJson(),
      "queryString"->JsString(queryString),
      "found" -> JsNumber(pubs.length),
      "lOption" -> lOptionJson
    ))
  }

  def toJsonString(): String = {
    Json.prettyPrint(toJson())
  }
}

class BSearcher(lOption: LOption, indexFolderString: String, topN: Int) extends LSearcher(lOption, indexFolderString, topN) {


  def search(queryString: String): BSearchResult = {
    val startTime = System.currentTimeMillis()
    validate(queryString)
    val queryOrNone = Option {
      val parser = new QueryParser(Config.COMBINED_FIELD, analyzer)
      parser.setAllowLeadingWildcard(true)
      parser.parse(queryString)
    }
    Logger.info(s"string=$queryString, query=$queryOrNone")
    queryOrNone match {
      case None => {
        val searchStats = SearchStats(0, queryOrNone)
        new BSearchResult(searchStats, queryString, Some(lOption), Array())
      }
      case Some(query) => {
        val allDocCollector = new TotalHitCountCollector()
        searcher.search(query, allDocCollector)
        val topDocs = searcher.search(query, topN)
        Logger.info(s"${allDocCollector.getTotalHits} hit docs")
        val duration = System.currentTimeMillis() - startTime
        val foundPubs = getSearchPub(topDocs, query)
        reader.close()
        val searchStats = SearchStats(duration, queryOrNone)
        new BSearchResult(searchStats, queryString, Some(lOption), foundPubs)
      }
    }
  }

  def getSearchPub(topDocs: TopDocs, query: Query): Array[BSearchPub] = {
    for (hit <- topDocs.scoreDocs) yield {
      val (docID, score) = (hit.doc, hit.score)
      val hitDoc = searcher.doc(docID)
      //      Logger.info(s"Explain: ${searcher.explain(query, docID)}")
      val fieldValues = for {
        field <- hitDoc.getFields
        if field.name() != Config.COMBINED_FIELD
      } yield field.name() -> field.stringValue()
      val fieldDocMap = Map(fieldValues: _*)
      new BSearchPub(docID, score, fieldDocMap)
    }
  }

}
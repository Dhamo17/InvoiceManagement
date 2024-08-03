package in.olc.GenerateInvoices

import in.olc.Services._

object GenerateInvoice {

  def main(args: Array[String]): Unit = {

    val inputPath = "D:\\InvoiceManagement_ScalaProject\\input\\invoice.xlsx"
    val outputDir = "D:\\InvoiceManagement_ScalaProject\\output"

   //  val yearmonthInvoice = new YearMonthWiseInvoice(inputPath,outputDir)
    //yearmonthInvoice.processInvoice()

    val studentIdWiseInvoice = new StudentIdWiseInvoice(inputPath,outputDir)
    studentIdWiseInvoice.processInvoice()

  }
}

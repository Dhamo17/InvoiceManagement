package in.olc.Services

import java.io.{File, FileInputStream}

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.{PdfDocument, PdfWriter}
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.{Cell, Image, Paragraph, Table}
import com.itextpdf.layout.property.TextAlignment
import in.olc.Utils.Student
import org.apache.poi.ss.usermodel.{DataFormatter, Row}
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class YearMonthWiseInvoice(inputPath:String,outputDir:String) {



  def processInvoice() = {

    readFromExcel()

  }




  def readFromExcel() = {

    val inpFile = new File(inputPath)
    val fis = new FileInputStream(inpFile)

    val InvoiceWorkBook = new XSSFWorkbook(fis)

    val InvoiceSheet =InvoiceWorkBook.getSheetAt(0)

    InvoiceSheet.forEach(row => {

      if(row.getRowNum()!=0) {

        val formatter = new DataFormatter()
        val studentId = formatter.formatCellValue(row.getCell(0,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
        val studentName = formatter.formatCellValue(row.getCell(1,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
        val studentEmail = formatter.formatCellValue(row.getCell(2,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
        val phno = formatter.formatCellValue(row.getCell(3,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
        val aadhar = formatter.formatCellValue(row.getCell(4,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
        val course = formatter.formatCellValue(row.getCell(5,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
        val fees = formatter.formatCellValue(row.getCell(6,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))

        val inst1 = formatter.formatCellValue(row.getCell(7,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
        val date1 = formatter.formatCellValue(row.getCell(8,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))

        val inst2 = formatter.formatCellValue(row.getCell(9,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
        val date2 = formatter.formatCellValue(row.getCell(10,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))

        val inst3 = formatter.formatCellValue(row.getCell(11,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
        val date3 = formatter.formatCellValue(row.getCell(12,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))

        val inst4 = formatter.formatCellValue(row.getCell(13,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
        val date4 = formatter.formatCellValue(row.getCell(14,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))

        var emi = Map[String,String]()
        if(!inst1.isEmpty())
          emi.+=(("01",inst1+"~"+date1))
        if(!inst2.isEmpty())
          emi.+=(("02",inst2+"~"+date2))
        if(!inst3.isEmpty())
          emi.+=(("03",inst3+"~"+date3))
        if(!inst4.isEmpty())
          emi.+=(("04",inst4+"~"+date4))

        val st = new Student(studentId.toInt,studentName,studentEmail,phno.toLong,aadhar,course,fees.toInt,emi)

        generatePdf(st,outputDir)

      }

    })

    fis.close()

  }

  def generatePdf(st: Student,outputDir:String) = {

    val dateFormat = "d-MMM-yy"
    val dtf = java.time.format.DateTimeFormatter.ofPattern(dateFormat)
    val tag = "YearMonthWiseInvoices"


    val logo = "C:\\Users\\Dhamo\\IdeaProjects\\InvoiceManagement\\src\\main\\scala\\in\\olc\\Utils\\OLCLogo.PNG"

    st.emi.foreach(kv => {

      val instdate = kv._2.split("~")

      val unitprice = (instdate(0).toInt - (instdate(0).toInt*18/100)).toString
      val gst = (instdate(0).toInt*18/100).toString

      val d = java.time.LocalDate.parse(instdate(1), dtf)
      val (year,mon,day) = (d.getYear,d.getMonth,d.getDayOfMonth)

      val fileName = st.studentId+"_"+st.studentName+"_"+st.studentCourse+"_"+instdate(0)+"_"+kv._1+"_"+instdate(1)+".pdf"


      val dir = outputDir+"\\"+tag+"\\"+year+"\\"+mon

      val directory = new File(dir)

      if(!directory.exists())
      {
        directory.mkdirs()

      }

      val outputFile =dir+"\\"+fileName

      val invoiceWriter = new PdfWriter(outputFile)

      val invoiceDocument = new PdfDocument(invoiceWriter)

      val doc = new Document(invoiceDocument)

      val imglogo = ImageDataFactory.create(logo)
      val img = new Image(imglogo)
      img.setMaxWidth(275)
      img.setMaxHeight(75)
      img.setFixedPosition(30,750);

      val invoiceStr = "INVOICE"

      val addressStr = "3rd Floor, Plot #53 \n Venkanna Hills, Chintal, Quthubullapur, Hyderabad, 500055"

      val dateandInvoice = instdate(1) + "\n Invoice # OLC-"+st.studentId.formatted("%04d")+"-"+kv._1

      val invoicePara = new Paragraph(invoiceStr)
      invoicePara.setTextAlignment(TextAlignment.RIGHT)
      invoicePara.setBold()
      invoicePara.setFontSize(13)
      invoicePara.setPaddingTop(20)

      val myTable = new Table(2)
      myTable.setWidth(550)
      myTable.addCell(getCell(addressStr, TextAlignment.LEFT,bold = false,padding = true))
      myTable.addCell(getCell(dateandInvoice, TextAlignment.RIGHT,bold = true,padding = true))

      val gstnPara = new Paragraph("GSTIN No: \u001B 36AACCO7284M1ZU")
      gstnPara.setFontSize(10)
      gstnPara.setPaddingTop(-5)

      val phnoandemail = "Phone : +91- 7 999 01 02 03 \n info@onlinelearningcenter.in"
      val invoiceto = "Invoice to\n "+st.studentName

      val phoneTable = new Table(2)
      phoneTable.setWidth(550)
      phoneTable.addCell(getCell(phnoandemail, TextAlignment.LEFT,bold = false,padding = true))
      phoneTable.addCell(getCell(invoiceto, TextAlignment.RIGHT,bold = true,padding = true))

      val custPhno = "Customer Mobile Number \t "+ st.studentPhno

      val customerMobileNumberTable = new Table(2)
      customerMobileNumberTable.setWidth(550)
      customerMobileNumberTable.addCell(getCell(custPhno, TextAlignment.RIGHT,bold = false,padding = true))

      val body = "Dear "+st.studentName+",\n\n"+"Please find the receipt of your Invoice for the month of "+mon+"-"+year+", paid as "+kv._1+"\nInstallment of the below course."

      val bodyPara = new Paragraph(body)
      bodyPara.setTextAlignment(TextAlignment.JUSTIFIED)
      bodyPara.setFontSize(10)


      val courseTable = new Table(5)
      courseTable.setWidth(550)
      courseTable.addHeaderCell("#")
      courseTable.addHeaderCell("Course Name")
      courseTable.addHeaderCell("Qty")
      courseTable.addHeaderCell("Unit Price(INR)")
      courseTable.addHeaderCell("Total(INR)")
      courseTable.addCell("1")
      courseTable.addCell(st.studentCourse)
      courseTable.addCell("1")
      courseTable.addCell(new Cell().add(new Paragraph(unitprice)))
      courseTable.addCell(new Cell().add(new Paragraph(unitprice)))

      val subTotalTable = new Table(2)
      subTotalTable.setWidth(550)
      subTotalTable.addCell(getCell("Subtotal \t\t\t"+unitprice, TextAlignment.RIGHT,bold = false,padding = true))

      val gstTable = new Table(2)
      gstTable.setWidth(550)
      gstTable.addCell(getCell("GST(18%) \t\t\t"+gst, TextAlignment.RIGHT,bold = false,padding = true))

      val totalTable = new Table(2)
      totalTable.setWidth(550)
      totalTable.addCell(getCell("Total \t\t\t"+instdate(0), TextAlignment.RIGHT,bold = false,padding = true))

      val lfTable = new Table(2)
      lfTable.setWidth(550)
      lfTable.addCell(getCell("Looking Forward,\n Online Learning Center Pvt Ltd", TextAlignment.RIGHT,bold = false,padding = true))

      val footerPara = new Paragraph("This is an e-bill and does not need any signature")
      footerPara.setTextAlignment(TextAlignment.CENTER)
      footerPara.setFontSize(10)



      doc.add(img)
      doc.add(invoicePara)
      doc.add(myTable)
      doc.add(gstnPara)
      doc.add(phoneTable)
      doc.add(customerMobileNumberTable)
      doc.add(bodyPara)
      doc.add(courseTable)
      doc.add(subTotalTable)
      doc.add(gstTable)
      doc.add(totalTable)
      doc.add(lfTable)
      doc.add(footerPara)
      doc.close()


    })

  }

  def getCell(str: String, alignment: TextAlignment,bold:Boolean=false,padding:Boolean=false):Cell= {
    val cell = new Cell().add(new Paragraph(str))
    if(padding)
      cell.setPadding(10)

    cell.setTextAlignment(alignment)
    cell.setBorder(Border.NO_BORDER)
    cell.setFontSize(10)
    if(bold)
      cell.setBold()
    return cell
  }

}

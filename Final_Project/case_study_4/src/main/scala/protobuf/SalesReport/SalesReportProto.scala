// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package protobuf.SalesReport

object SalesReportProto extends _root_.scalapb.GeneratedFileObject {
  lazy val dependencies: Seq[_root_.scalapb.GeneratedFileObject] = Seq.empty
  lazy val messagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] =
    Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]](
      protobuf.SalesReport.SalesReport
    )
  private lazy val ProtoBytes: _root_.scala.Array[Byte] =
      scalapb.Encoding.fromBase64(scala.collection.immutable.Seq(
  """ChFTYWxlc1JlcG9ydC5wcm90bxIIcHJvdG9idWYi0QEKC1NhbGVzUmVwb3J0EiAKBXN0b3JlGAEgASgJQgriPwcSBXN0b3JlU
  gVzdG9yZRIdCgRkZXB0GAIgASgJQgniPwYSBGRlcHRSBGRlcHQSHQoEZGF0ZRgDIAEoCUIJ4j8GEgRkYXRlUgRkYXRlEjMKDHdlZ
  WtseV9zYWxlcxgEIAEoAUIQ4j8NEgt3ZWVrbHlTYWxlc1ILd2Vla2x5U2FsZXMSLQoKaXNfaG9saWRheRgFIAEoCEIO4j8LEglpc
  0hvbGlkYXlSCWlzSG9saWRheWIGcHJvdG8z"""
      ).mkString)
  lazy val scalaDescriptor: _root_.scalapb.descriptors.FileDescriptor = {
    val scalaProto = com.google.protobuf.descriptor.FileDescriptorProto.parseFrom(ProtoBytes)
    _root_.scalapb.descriptors.FileDescriptor.buildFrom(scalaProto, dependencies.map(_.scalaDescriptor))
  }
  lazy val javaDescriptor: com.google.protobuf.Descriptors.FileDescriptor = {
    val javaProto = com.google.protobuf.DescriptorProtos.FileDescriptorProto.parseFrom(ProtoBytes)
    com.google.protobuf.Descriptors.FileDescriptor.buildFrom(javaProto, _root_.scala.Array(
    ))
  }
  @deprecated("Use javaDescriptor instead. In a future version this will refer to scalaDescriptor.", "ScalaPB 0.5.47")
  def descriptor: com.google.protobuf.Descriptors.FileDescriptor = javaDescriptor
}
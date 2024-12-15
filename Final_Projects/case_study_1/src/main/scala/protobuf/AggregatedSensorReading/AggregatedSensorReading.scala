// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package protobuf.AggregatedSensorReading

@SerialVersionUID(0L)
final case class AggregatedSensorReading(
    sensorId: _root_.scala.Int = 0,
    averageTemperature: _root_.scala.Float = 0.0f,
    averageHumidity: _root_.scala.Float = 0.0f,
    minimumTemperature: _root_.scala.Float = 0.0f,
    maximumTemperature: _root_.scala.Float = 0.0f,
    minimumHumidity: _root_.scala.Float = 0.0f,
    maximumHumidity: _root_.scala.Float = 0.0f,
    noOfRecords: _root_.scala.Long = 0L,
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[AggregatedSensorReading] {
    @transient
    private[this] var __serializedSizeMemoized: _root_.scala.Int = 0
    private[this] def __computeSerializedSize(): _root_.scala.Int = {
      var __size = 0
      
      {
        val __value = sensorId
        if (__value != 0) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeInt32Size(1, __value)
        }
      };
      
      {
        val __value = averageTemperature
        if (__value != 0.0f) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeFloatSize(2, __value)
        }
      };
      
      {
        val __value = averageHumidity
        if (__value != 0.0f) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeFloatSize(3, __value)
        }
      };
      
      {
        val __value = minimumTemperature
        if (__value != 0.0f) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeFloatSize(4, __value)
        }
      };
      
      {
        val __value = maximumTemperature
        if (__value != 0.0f) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeFloatSize(5, __value)
        }
      };
      
      {
        val __value = minimumHumidity
        if (__value != 0.0f) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeFloatSize(6, __value)
        }
      };
      
      {
        val __value = maximumHumidity
        if (__value != 0.0f) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeFloatSize(7, __value)
        }
      };
      
      {
        val __value = noOfRecords
        if (__value != 0L) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeInt64Size(8, __value)
        }
      };
      __size += unknownFields.serializedSize
      __size
    }
    override def serializedSize: _root_.scala.Int = {
      var __size = __serializedSizeMemoized
      if (__size == 0) {
        __size = __computeSerializedSize() + 1
        __serializedSizeMemoized = __size
      }
      __size - 1
      
    }
    def writeTo(`_output__`: _root_.com.google.protobuf.CodedOutputStream): _root_.scala.Unit = {
      {
        val __v = sensorId
        if (__v != 0) {
          _output__.writeInt32(1, __v)
        }
      };
      {
        val __v = averageTemperature
        if (__v != 0.0f) {
          _output__.writeFloat(2, __v)
        }
      };
      {
        val __v = averageHumidity
        if (__v != 0.0f) {
          _output__.writeFloat(3, __v)
        }
      };
      {
        val __v = minimumTemperature
        if (__v != 0.0f) {
          _output__.writeFloat(4, __v)
        }
      };
      {
        val __v = maximumTemperature
        if (__v != 0.0f) {
          _output__.writeFloat(5, __v)
        }
      };
      {
        val __v = minimumHumidity
        if (__v != 0.0f) {
          _output__.writeFloat(6, __v)
        }
      };
      {
        val __v = maximumHumidity
        if (__v != 0.0f) {
          _output__.writeFloat(7, __v)
        }
      };
      {
        val __v = noOfRecords
        if (__v != 0L) {
          _output__.writeInt64(8, __v)
        }
      };
      unknownFields.writeTo(_output__)
    }
    def withSensorId(__v: _root_.scala.Int): AggregatedSensorReading = copy(sensorId = __v)
    def withAverageTemperature(__v: _root_.scala.Float): AggregatedSensorReading = copy(averageTemperature = __v)
    def withAverageHumidity(__v: _root_.scala.Float): AggregatedSensorReading = copy(averageHumidity = __v)
    def withMinimumTemperature(__v: _root_.scala.Float): AggregatedSensorReading = copy(minimumTemperature = __v)
    def withMaximumTemperature(__v: _root_.scala.Float): AggregatedSensorReading = copy(maximumTemperature = __v)
    def withMinimumHumidity(__v: _root_.scala.Float): AggregatedSensorReading = copy(minimumHumidity = __v)
    def withMaximumHumidity(__v: _root_.scala.Float): AggregatedSensorReading = copy(maximumHumidity = __v)
    def withNoOfRecords(__v: _root_.scala.Long): AggregatedSensorReading = copy(noOfRecords = __v)
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = sensorId
          if (__t != 0) __t else null
        }
        case 2 => {
          val __t = averageTemperature
          if (__t != 0.0f) __t else null
        }
        case 3 => {
          val __t = averageHumidity
          if (__t != 0.0f) __t else null
        }
        case 4 => {
          val __t = minimumTemperature
          if (__t != 0.0f) __t else null
        }
        case 5 => {
          val __t = maximumTemperature
          if (__t != 0.0f) __t else null
        }
        case 6 => {
          val __t = minimumHumidity
          if (__t != 0.0f) __t else null
        }
        case 7 => {
          val __t = maximumHumidity
          if (__t != 0.0f) __t else null
        }
        case 8 => {
          val __t = noOfRecords
          if (__t != 0L) __t else null
        }
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PInt(sensorId)
        case 2 => _root_.scalapb.descriptors.PFloat(averageTemperature)
        case 3 => _root_.scalapb.descriptors.PFloat(averageHumidity)
        case 4 => _root_.scalapb.descriptors.PFloat(minimumTemperature)
        case 5 => _root_.scalapb.descriptors.PFloat(maximumTemperature)
        case 6 => _root_.scalapb.descriptors.PFloat(minimumHumidity)
        case 7 => _root_.scalapb.descriptors.PFloat(maximumHumidity)
        case 8 => _root_.scalapb.descriptors.PLong(noOfRecords)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion: protobuf.AggregatedSensorReading.AggregatedSensorReading.type = protobuf.AggregatedSensorReading.AggregatedSensorReading
    // @@protoc_insertion_point(GeneratedMessage[protobuf.AggregatedSensorReading])
}

object AggregatedSensorReading extends scalapb.GeneratedMessageCompanion[protobuf.AggregatedSensorReading.AggregatedSensorReading] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[protobuf.AggregatedSensorReading.AggregatedSensorReading] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): protobuf.AggregatedSensorReading.AggregatedSensorReading = {
    var __sensorId: _root_.scala.Int = 0
    var __averageTemperature: _root_.scala.Float = 0.0f
    var __averageHumidity: _root_.scala.Float = 0.0f
    var __minimumTemperature: _root_.scala.Float = 0.0f
    var __maximumTemperature: _root_.scala.Float = 0.0f
    var __minimumHumidity: _root_.scala.Float = 0.0f
    var __maximumHumidity: _root_.scala.Float = 0.0f
    var __noOfRecords: _root_.scala.Long = 0L
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 8 =>
          __sensorId = _input__.readInt32()
        case 21 =>
          __averageTemperature = _input__.readFloat()
        case 29 =>
          __averageHumidity = _input__.readFloat()
        case 37 =>
          __minimumTemperature = _input__.readFloat()
        case 45 =>
          __maximumTemperature = _input__.readFloat()
        case 53 =>
          __minimumHumidity = _input__.readFloat()
        case 61 =>
          __maximumHumidity = _input__.readFloat()
        case 64 =>
          __noOfRecords = _input__.readInt64()
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    protobuf.AggregatedSensorReading.AggregatedSensorReading(
        sensorId = __sensorId,
        averageTemperature = __averageTemperature,
        averageHumidity = __averageHumidity,
        minimumTemperature = __minimumTemperature,
        maximumTemperature = __maximumTemperature,
        minimumHumidity = __minimumHumidity,
        maximumHumidity = __maximumHumidity,
        noOfRecords = __noOfRecords,
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[protobuf.AggregatedSensorReading.AggregatedSensorReading] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      protobuf.AggregatedSensorReading.AggregatedSensorReading(
        sensorId = __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Int]).getOrElse(0),
        averageTemperature = __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.Float]).getOrElse(0.0f),
        averageHumidity = __fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).map(_.as[_root_.scala.Float]).getOrElse(0.0f),
        minimumTemperature = __fieldsMap.get(scalaDescriptor.findFieldByNumber(4).get).map(_.as[_root_.scala.Float]).getOrElse(0.0f),
        maximumTemperature = __fieldsMap.get(scalaDescriptor.findFieldByNumber(5).get).map(_.as[_root_.scala.Float]).getOrElse(0.0f),
        minimumHumidity = __fieldsMap.get(scalaDescriptor.findFieldByNumber(6).get).map(_.as[_root_.scala.Float]).getOrElse(0.0f),
        maximumHumidity = __fieldsMap.get(scalaDescriptor.findFieldByNumber(7).get).map(_.as[_root_.scala.Float]).getOrElse(0.0f),
        noOfRecords = __fieldsMap.get(scalaDescriptor.findFieldByNumber(8).get).map(_.as[_root_.scala.Long]).getOrElse(0L)
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = AggregatedSensorReadingProto.javaDescriptor.getMessageTypes().get(0)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = AggregatedSensorReadingProto.scalaDescriptor.messages(0)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = throw new MatchError(__number)
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = protobuf.AggregatedSensorReading.AggregatedSensorReading(
    sensorId = 0,
    averageTemperature = 0.0f,
    averageHumidity = 0.0f,
    minimumTemperature = 0.0f,
    maximumTemperature = 0.0f,
    minimumHumidity = 0.0f,
    maximumHumidity = 0.0f,
    noOfRecords = 0L
  )
  implicit class AggregatedSensorReadingLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, protobuf.AggregatedSensorReading.AggregatedSensorReading]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, protobuf.AggregatedSensorReading.AggregatedSensorReading](_l) {
    def sensorId: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Int] = field(_.sensorId)((c_, f_) => c_.copy(sensorId = f_))
    def averageTemperature: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Float] = field(_.averageTemperature)((c_, f_) => c_.copy(averageTemperature = f_))
    def averageHumidity: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Float] = field(_.averageHumidity)((c_, f_) => c_.copy(averageHumidity = f_))
    def minimumTemperature: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Float] = field(_.minimumTemperature)((c_, f_) => c_.copy(minimumTemperature = f_))
    def maximumTemperature: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Float] = field(_.maximumTemperature)((c_, f_) => c_.copy(maximumTemperature = f_))
    def minimumHumidity: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Float] = field(_.minimumHumidity)((c_, f_) => c_.copy(minimumHumidity = f_))
    def maximumHumidity: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Float] = field(_.maximumHumidity)((c_, f_) => c_.copy(maximumHumidity = f_))
    def noOfRecords: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Long] = field(_.noOfRecords)((c_, f_) => c_.copy(noOfRecords = f_))
  }
  final val SENSORID_FIELD_NUMBER = 1
  final val AVERAGETEMPERATURE_FIELD_NUMBER = 2
  final val AVERAGEHUMIDITY_FIELD_NUMBER = 3
  final val MINIMUMTEMPERATURE_FIELD_NUMBER = 4
  final val MAXIMUMTEMPERATURE_FIELD_NUMBER = 5
  final val MINIMUMHUMIDITY_FIELD_NUMBER = 6
  final val MAXIMUMHUMIDITY_FIELD_NUMBER = 7
  final val NOOFRECORDS_FIELD_NUMBER = 8
  def of(
    sensorId: _root_.scala.Int,
    averageTemperature: _root_.scala.Float,
    averageHumidity: _root_.scala.Float,
    minimumTemperature: _root_.scala.Float,
    maximumTemperature: _root_.scala.Float,
    minimumHumidity: _root_.scala.Float,
    maximumHumidity: _root_.scala.Float,
    noOfRecords: _root_.scala.Long
  ): _root_.protobuf.AggregatedSensorReading.AggregatedSensorReading = _root_.protobuf.AggregatedSensorReading.AggregatedSensorReading(
    sensorId,
    averageTemperature,
    averageHumidity,
    minimumTemperature,
    maximumTemperature,
    minimumHumidity,
    maximumHumidity,
    noOfRecords
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[protobuf.AggregatedSensorReading])
}
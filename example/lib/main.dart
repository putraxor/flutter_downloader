import 'package:flutter/material.dart';
import 'package:flutter_downloader/flutter_downloader.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final url = new TextEditingController(
      text: 'https://flutter.io/images/flutter-mark-square-100.png');
  String status = '';

  @override
  initState() {
    super.initState();
  }

  download() {
    FlutterDownloader.download(url.text, 'flutter.png').then((result) {
      setState(() {
        status = result;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: new Text('Example'),
        ),
        body: new Padding(
          padding: const EdgeInsets.all(16.0),
          child: new Column(
            children: <Widget>[
              new Text('Enter download url'),
              new TextFormField(controller: url),
              new SizedBox(height: 8.0),
              new RaisedButton(
                child: new Text('Download Now'),
                onPressed: () => download(),
              ),
              new Divider(),
              new Text(status)
            ],
          ),
        ),
      ),
    );
  }
}

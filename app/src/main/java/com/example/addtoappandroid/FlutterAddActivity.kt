package com.example.addtoappandroid
// Copyright 2020 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.


import io.flutter.embedding.android.FlutterActivity

/**
 * This {@link FlutterActivity} class repackages Kotlin-Dart interop using the Pigeon IPC mechanism.
 * It repackages Flutter/Dart-side functionalities in standard Android API style, passing
 * arguments in and out of the activity using 'startActivityForResult' intents and
 * 'onActivityResult' intents.
 */
// class HostBookApiHandler: Api.HostAppApi {
//    override fun cancel() {
//        TODO("Not yet implemented")
//    }
//
//    override fun authSuccess(token: Api.Token) {
//        TODO("Not yet implemented")
//    }
//
//}

class FlutterAddActivity: FlutterActivity() {

    override fun getCachedEngineId(): String {
        return "book_engine"
    }

//    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
//        // Called shortly after the activity is created, when the activity is bound to a
//        // FlutterEngine responsible for rendering the Flutter activity's content.
//        Log.d("cheche", "adsf")
//        super.configureFlutterEngine(flutterEngine)
//        // The book to give to Flutter is passed in from the MainActivity via this activity's
//        // source intent getter. The intent contains the book serialized as on extra.
//        val token = Api.Token();
//
//        // Register the HostBookApiHandler callback class to get results from Flutter.
//        HostAppApi.setUp(flutterEngine.dartExecutor, HostAppApiHandler())
//
//        // Send in the book instance to Flutter.
//        Api.FlutterAppApi(flutterEngine.dartExecutor).displayAuth(token, object : VoidResult {
//            override fun success() {
//                TODO("Not yet implemented")
//            }
//
//            override fun error(error: Throwable) {
//                TODO("Not yet implemented")
//            }
//        });
//    }

    // This {@link Api.HostBookApi} subclass will be called by Pigeon when the corresponding
    // APIs are invoked on the Dart side.
//    inner class HostAppApiHandler: HostAppApi {
//        override fun cancel() {
//            // Flutter called cancel. Finish the activity with a cancel result.
//            Log.d("hello", "mello")
//            setResult(Activity.RESULT_CANCELED)
//            finish()
//        }
//
//        override fun finishAuthWithToken(token: Api.Token) {
//            setResult(Activity.RESULT_CANCELED)
//            finish()
//        }
//
////        override fun finishEditingBook(book: Api.Book?) {
////            if (book == null) {
////                throw IllegalArgumentException("finishedEditingBook cannot be called with a null argument")
////            }
////            // Flutter returned an edited book instance. Return it to the MainActivity via the
////            // standard Android Activity set result mechanism.
////            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_BOOK, HashMap(book.toMap())))
////            finish()
////        }
//    }
}

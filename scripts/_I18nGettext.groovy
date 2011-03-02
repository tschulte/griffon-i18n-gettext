includeTargets << griffonScript("_GriffonInit")
includeTargets << gant.targets.Clean
includeTool << gant.tools.Execute

def getConfigValue = { what->
    try {
       switch(what) {
            case "inputFileCharset":
                return buildConfig?.i18n?.inputFileCharset ?: "UTF-8"
            case "excludedDirs":
                return buildConfig?.i18n?.excludedDirs ?: []
            case "bundleName":
                return buildConfig?.i18n?.bundleName ?: "Messages"
            case "xgettextParams":
                return buildConfig?.i18n?.xgettextParams ?: "--add-comments"
            default:
                return null
        }
            
    } catch (Exception e) { 
        // ignore 
    }
    
    return null;
}


target( scan:"Generate .pot file from sources" ){
        
    println("\nGenerating .pot file from sources.")

    def charset = getConfigValue("inputFileCharset")
    def excludedDirs = getConfigValue("excludedDirs")
    def xgettextParams = getConfigValue("xgettextParams")

    // trash the last .pot file
    def keysFileName = "${i18nDir}/keys.pot"
    def tmpKeysFileName = "${keysFileName}.tmp"
    new File( tmpKeysFileName ).write("")
    
    new File(".").eachFileRecurse{ file ->
        def currentFileCanonicalPath = file.getCanonicalPath()
        
        def skipThis = false
        excludedDirs.any { 
            if( currentFileCanonicalPath.startsWith( new File(it).getCanonicalPath() ) ){
                skipThis = true
            }
        } 
        
        if( !skipThis ){
            if( file.isFile() ){
                // switch programming language identifier for best recognition rates
                def programmingLanguageIdentifier = ""
                if( file.name.endsWith(".groovy") || file.name.endsWith(".java") ){
                    programmingLanguageIdentifier = "java"
                } 
                        
                if( programmingLanguageIdentifier.length()>0 ) {
                    def command = "xgettext --join-existing --force-po -ktrc:1c,2 -ktr -kmarktr -ktrn:1,2 -ktrnc:1c,2,3 ${xgettextParams} --from-code=${charset} -o ${tmpKeysFileName} -L${programmingLanguageIdentifier} ${file.getCanonicalPath()}"
                    
                    println( command )
                    def e = command.execute()
                    e.waitFor()
                    if( e.exitValue() ){
                        println( "Error: ${e.err.text}")
                    }
                }
            }
        }
    }
    new File(keysFileName).withPrintWriter(charset) { printWriter ->
        new File(tmpKeysFileName).eachLine(charset) { line ->
            if (line.startsWith('msgid'))
                printWriter.println(line.replace(/\\$/, '$'))
            else
                printWriter.println(line)
            line
        }
    }
    new File(tmpKeysFileName).delete()
    mergepo()
}



target( mergepo:"Merging .po files with .pot file" ){

    println( "\nMerging .po files with .pot file." )
    fileNameToCreate = "default"
    touchpo()        // the default Resource

    List fl = new File(i18nDir).listFiles([accept:{file->file ==~ /.*?\.po/ }] as FileFilter).toList().name
    def noWrap = getConfigValue( "noWrapPoLines" )?"--no-wrap":""

    fl.each(){
        if( !it.contains('~') ){
            String lang = it.replace( ".po", "" )

            command = "msgmerge ${noWrap} --backup=off -U ${i18nDir}/${lang}.po ${i18nDir}/keys.pot"
            println( command )
            def e = command.execute()
            e.waitFor()
            if( e.exitValue() ){
                println( "Error: ${e.err.text}")
            }
        }
    }
}


target( makemo:"Compile .mo files" ){
    println("\nCompiling .mo files.")

    def i18nOutputDir = 'target/i18n-gettext'
    def destination = new File( i18nOutputDir );
    if( !destination.exists() ){
        destination.mkdirs()
    }
    
    def i18nOutputDirCanonical = destination.getCanonicalPath()
    def bundleName = getConfigValue("bundleName")

    List fl = new File(i18nDir).listFiles([accept:{file->file ==~ /.*?\.po/ }] as FileFilter) as List
    fl.each(){ poFile ->
        if( !poFile.name.contains('~') ) {
            String lang = poFile.name.replace(".po", "").replace('default', '')
            def classFile = new File(i18nOutputDir, "i18ngettext/${bundleName}${lang? '_' + lang : ''}.class")
            if (classFile.exists() && classFile.lastModified() >= poFile.lastModified())
                return
            command = "msgfmt --java2 -d ${i18nOutputDirCanonical} -r i18ngettext.${bundleName} ${poFile.getCanonicalPath()}" // the default Resource
            if (lang) {
                command += " -l ${lang}"
            }

            println( command )
            def e = command.execute()
            e.waitFor()
            if( e.exitValue() ){
                println("Error: ${e.err.text}")
            }
        }
    }
    
    ant.jar( basedir:"${i18nOutputDirCanonical}", includes:"i18ngettext/*", destfile:"./lib/i18n-gettext-${bundleName}.jar")
}


target( touchpo:"Initialize first .po file" ) { params->

    def charset = getConfigValue( "inputFileCharset" )
    def header = """
# SOME DESCRIPTIVE TITLE.
# Copyright (C) YEAR AUTHOR
# This file is distributed under the same license as the PACKAGE package.
# FIRST AUTHOR <EMAIL@ADDRESS>, YEAR.
#
#, fuzzy
msgid ""
msgstr ""
"Project-Id-Version: PACKAGE VERSION\\n"
"Report-Msgid-Bugs-To: \\n"
"POT-Creation-Date: YEAR-MO-DA HO:MO+ZONE\\n"
"PO-Revision-Date: YEAR-MO-DA HO:MI+ZONE\\n"
"Last-Translator: FULL NAME <EMAIL@ADDRESS>\\n"
"Language-Team: LANGUAGE <LL@li.org>\\n"
"MIME-Version: 1.0\\n"
"Content-Type: text/plain; charset=${charset}\\n"
"Content-Transfer-Encoding: 8bit\\n"
"""
    if( fileNameToCreate.length()>0 ){
        
        def fileName = fileNameToCreate.replace(".po", "")
        def destination = new File(i18nDir, "${fileName}.po")

        if( destination.exists() ){
            if( fileName != "default" ){
                println( "File: ${destination.getCanonicalPath()} already exists. Will not recreate it.")
            }
        } else {
            if( fileName=="default" ){
                // write our default header to the file
                destination.write( header, 'UTF-8' )
            } else {
                // make sure the "default.po" file exists
                fileNameToCreate = "default"
                touchpo()

                def source = new File(i18nDir, 'default.po')
                if( source ){
                    // copy the "default.po" file to the new name.
                    destination.withOutputStream{ out->out.write source.readBytes() }
                } else {
                    // write our default header to the file
                    destination.write( header, 'UTF-8' )
                }
                println("File: ${destination.getCanonicalPath()} has been created.")
            }
        }
    }
}
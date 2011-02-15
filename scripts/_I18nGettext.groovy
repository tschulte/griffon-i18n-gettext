includeTargets << griffonScript("_GriffonInit")
includeTargets << gant.targets.Clean
includeTool << gant.tools.Execute

def getConfigValue = { what->

    def result = null

    try {
       switch( what ){
            case "inputFileCharset":
                result = config?.i18n?.inputFileCharset?:"UTF-8"
                return result 
            break
    
            case "excludedDirsArray":
                result = config?.i18n?.excludedDirsArray?:[]
                return result 
            break
    
            case "noWrapPoLines":
                result = config?.i18n?.noWrapPoLines?true:false
                return result 
            break
            
            case "bundleName":
                result = config?.i18n?.bundleName?:"Messages"
                return result 
            break

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

    def charset = getConfigValue( "inputFileCharset" )
    def excludedDirsArray = getConfigValue( "excludedDirsArray" )
    def noWrap = getConfigValue( "noWrapPoLines" )?"--no-wrap":""

    // trash the last .pot file
    def keysFileName = "${i18nDir}/keys.pot"
    new File( keysFileName ).write("")
    
    new File(".").eachFileRecurse{ file ->
        def currentFileCanonicalPath = file.getCanonicalPath()
        
        def skipThis = false
        excludedDirsArray.any { 
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
                        
                if( programmingLanguageIdentifier.length()>0 ){
                    def command = "xgettext -j --add-comments --force-po ${noWrap} -ktrc -ktr -kmarktr -ktrn:1,2 --from-code=${charset} -o ${i18nDir}/keys.pot -L${programmingLanguageIdentifier} ${file.getCanonicalPath()}"
                    
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

    def destination = new File( i18nOutputDir );
    if( !destination.exists() ){
        destination.mkdir()
    }
    
    def i18nOutputDirCanonical = destination.getCanonicalPath()
    def bundleName = getConfigValue("bundleName")

    List fl = new File(i18nDir).listFiles([accept:{file->file ==~ /.*?\.po/ }] as FileFilter).toList().name
    fl.each(){
        if( !it.contains('~') ) {
            String lang = it.replace( ".po", "" )

            command = "msgfmt --java2 -d ${i18nOutputDirCanonical} -r i18ngettext.${bundleName} ${i18nDir}/${lang}.po" // the default Resource
            if( lang!="default" ) {
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
    new File(destination, "i18ngettext").deleteDir()
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
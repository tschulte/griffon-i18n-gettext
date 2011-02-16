//
//   Copyright 2008,2009,2010 Rainer Brang
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//

i18nDir = "./griffon-app/i18n"
fileNameToCreate = null

includeTargets << griffonScript("_GriffonInit")
includeTargets << griffonScript("_GriffonPackage")
includeTargets << gant.targets.Clean
includeTool << gant.tools.Execute

includeTargets << new File("${i18nGettextPluginDir}/scripts/_I18nGettext.groovy")

target( main: "Scan all .groovy files for tr() trn() and merge with all .po files in "+i18nDir ) {
	
    parameters = []

    if( args ){
        parameters = args.split("\n")
    }

    switch( parameters[0] ){
        case 'init':
        case 'touchpo':
            fileNameToCreate = "default"
            if(parameters.size() > 1) {
                fileNameToCreate = parameters[1]
            }
            touchpo()
            break
        case 'makemo':
            makemo()
            break
        case 'merge':
            mergepo()
            break
        case 'scan':
        default:
            scan()
    }
}

setDefaultTarget(main)


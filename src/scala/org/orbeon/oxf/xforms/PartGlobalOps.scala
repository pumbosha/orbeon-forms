/**
 * Copyright (C) 2011 Orbeon, Inc.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The full text of the license is available at http://www.gnu.org/copyleft/lesser.html
 */
package org.orbeon.oxf.xforms

import analysis.controls._
import analysis.ElementAnalysis
import analysis.model.Instance
import event.EventHandler
import org.dom4j.{Element, QName}
import java.util.{List ⇒ JList, Map ⇒ JMap}
import org.orbeon.oxf.xml.SAXStore
import xbl.{Scope, XBLBindings, ConcreteBinding}
import org.apache.commons.lang.StringUtils

trait PartGlobalOps {

    // Global
    def getMark(prefixedId: String): SAXStore#Mark

    // Models
    def getInstances(modelPrefixedId: String): java.util.Collection[Instance]

    // Controls
    def getControlAnalysis(prefixedId: String): ElementAnalysis
    def hasControlByName(controlName: String): Boolean
    def hasControlAppearance(controlName: String, appearance: QName): Boolean
    def hasInputPlaceholder: Boolean

    // Events
    def hasHandlerForEvent(eventName: String): Boolean
    def hasHandlerForEvent(eventName: String, includeAllEvents: Boolean): Boolean
    def getKeyHandlers: JList[EventHandler]

    // XBL
    def isComponent(binding: QName): Boolean
    def getBinding(prefixedId: String): ConcreteBinding
    def getBindingId(prefixedId: String): String
    def getBindingQNames: Seq[QName]
    def getGlobals: collection.Map[QName, XBLBindings#Global]
    def getResolutionScopeByPrefix(prefix: String): Scope
    def getResolutionScopeByPrefixedId(prefixedId: String): Scope

    // Repeats
    def addMissingRepeatIndexes(repeatIdToIndex: JMap[String, java.lang.Integer])
    def getRepeatHierarchyString: String

    // AVTs
    def hasAttributeControl(prefixedForAttribute: String): Boolean
    def getAttributeControl(prefixedForAttribute: String, attributeName: String): AttributeControl

    // Client-side resources
    def scripts: Map[String, Script]
    def uniqueClientScripts: Seq[(String, String)]
    def getXBLStyles: Seq[Element]
    def getXBLScripts: Seq[Element]
    def baselineResources: (collection.Set[String], collection.Set[String])

    // Functions derived from getControlAnalysis
    def getControlAnalysisOption(prefixedId: String) = Option(getControlAnalysis(prefixedId))
    def getControlElement(prefixedId: String) = getControlAnalysisOption(prefixedId) map (_.element) orNull
    def hasNodeBinding(prefixedId: String) = getControlAnalysisOption(prefixedId) map (_.hasNodeBinding) getOrElse false

    def getControlPosition(prefixedId: String) = getControlAnalysisOption(prefixedId) match {
        case Some(viewTrait: ViewTrait) ⇒ viewTrait.index
        case _ ⇒ -1
    }

    def getSelect1Analysis(prefixedId: String) = getControlAnalysisOption(prefixedId) match {
        case Some(selectionControl: SelectionControl) ⇒ selectionControl
        case _ ⇒ null
    }

    def isValueControl(effectiveId: String) =
        getControlAnalysisOption(XFormsUtils.getPrefixedId(effectiveId)) map (_.canHoldValue) getOrElse false

    def appendClasses(sb: java.lang.StringBuilder, prefixedId: String) =
        getControlAnalysisOption(prefixedId) foreach { controlAnalysis ⇒
            val controlClasses = controlAnalysis.classes
            if (StringUtils.isNotEmpty(controlClasses)) {
                if (sb.length > 0)
                    sb.append(' ')
                sb.append(controlClasses)
            }
        }

    def getLabel(prefixedId: String) = getLHHA(prefixedId, "label")
    def getHelp(prefixedId: String) = getLHHA(prefixedId, "help")
    def getHint(prefixedId: String) = getLHHA(prefixedId, "hint")
    def getAlert(prefixedId: String) = getLHHA(prefixedId, "alert")

    private def getLHHA(prefixedId: String, lhha: String) =
        getControlAnalysisOption(prefixedId) match {
            case Some(lhhaTrait: LHHATrait) ⇒ lhhaTrait.getLHHA(lhha).orNull
            case _ ⇒ null
        }
}
/**
 *  Porch And Hall Light Control
 *
 *  Copyright 2017 Chris Jordan
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
  name: "Front Door Activity",
  namespace: "theceej",
  author: "Chris Jordan",
  description: "Control lights in porch based on sensors on two doors",
  category: "Convenience",
  iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
  iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
  iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
  section("External door sensor:") {
    input "extDoor", "capability.contactSensor", required: true
  }
  section("Internal door sensor:") {
    input "intDoor", "capability.contactSensor", required: true
  }
  section("Porch light:") {
    input "porchLight", "capability.switch", required: true
  }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialise()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialise()
}

def initialise() {
  subscribe(extDoor, "contact.open", extDoorOpenedHandler)
  subscribe(intDoor, "contact.open", intDoorOpenedHandler)
  subscribe(extDoor, "contact.closed", extDoorClosedHandler)
  subscribe(intDoor, "contact.closed", intDoorClosedHandler)
}

def extDoorOpenedHandler(evt) {
  if (state.internalTriggered) {
    state.direction = 'leaving'
  } else {
    state.direction = 'arriving'
  }
  state.internalTriggered = false
  log.debug("External opened")
}

def intDoorOpenedHandler(evt) {
  state.internalTriggered = true
  log.debug("Internal opened")
}

def extDoorClosedHandler(evt) {
  log.debug(state.direction)
}
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
  iconUrl: 'http://cdn.device-icons.smartthings.com/Lighting/light15-icn.png',
  iconX2Url: 'http://cdn.device-icons.smartthings.com/Lighting/light15-icn@2x.png'
  iconX3Url: 'http://cdn.device-icons.smartthings.com/Lighting/light15-icn@3x.png')


preferences {
  section('Door contact sensors:') {
    input 'extDoor', 'capability.contactSensor', required: true, title: 'External door'
    input 'intDoor', 'capability.contactSensor', required: true, title: 'Internal door'
  }
  section('Light:') {
    input 'porchLight', 'capability.switch', required: true, title: 'Porch light'
  }
  section('Timer:') {
    input 'lightTimeout', 'number', required: true, title: 'Porch light timeout (seconds)'
  }
}

def installed() {
	log.debug 'Installed with settings: ${settings}'

	initialise()
}

def updated() {
	log.debug 'Updated with settings: ${settings}'

	unsubscribe()
	initialise()
}

def initialise() {
  subscribe(extDoor, 'contact.open', extDoorOpenedHandler)
  subscribe(intDoor, 'contact.open', intDoorOpenedHandler)
  subscribe(extDoor, 'contact.closed', extDoorClosedHandler)
}

def extDoorOpenedHandler(evt) {
  if (state.internalTriggered) {
    state.direction = 'leaving'
  } else {
    state.direction = 'arriving'
  }
  state.internalTriggered = false
  porchLight.on()
  runIn(lightTimeout, timeoutHandler)
  log.debug("External opened")
}

def intDoorOpenedHandler(evt) {
  state.internalTriggered = true
  porchLight.on()
  runIn(lightTimeout, timeoutHandler)
  log.debug("Internal opened")
}

def extDoorClosedHandler(evt) {
  log.debug(state.direction)
}

def timeoutHandler() {
  log.debug('Timed out')
  porchLight.off()
}
from __future__ import print_function
from pox.core import core
import pox.openflow.libopenflow_01 as of

log = core.getLogger()
# mac_to_ports[event.dpid] is the data structure.
# It is a nested dictionary in the form: {Switch_number: {MAC address: Port}, ...}
mac_to_ports = {}


class Switch:
    def __init__(self, connection):
        self.connection = connection
        connection.addListenerByName("PacketIn", self._handle_PacketIn)
        connection.addListenerByName("ConnectionUp", self._handle_ConnectionUp)

    def _handle_ConnectionUp(self, event):
        print("Switch {} connected".format(event.connection.dpid))
        mac_to_ports[event.connection.dpid] = {}

    def _handle_PacketIn(self, event):
        # Get the switch number, packet, and packet data
        switch_number = event.connection.dpid
        packet = event.parsed
        data = event.ofp

        print("\n{}\t{}\t{}".format(switch_number, packet.src, packet.dst))

        # Add the source of the packet to the controller's table if it is not there
        if packet.src not in mac_to_ports[switch_number]:
            print("Discovered {} on port {}".format(packet.src, data.in_port))
            mac_to_ports[switch_number][packet.src] = data.in_port

        # if a multicast packet, skip graph accessing, for destination
        # just flood
        if packet.dst.is_multicast:
            self.send_packet(event, of.OFPP_FLOOD)
        else:
            # Check if destination is not known in our hashmap
            if packet.dst not in mac_to_ports[switch_number]:
                print("Unknown destination {} flooding...".format(packet.dst))
                self.send_packet(event, of.OFPP_FLOOD)
            else:
                # Add flow entry for source and destination match. Source added since our discovery is source based
                out_port = mac_to_ports[switch_number][packet.dst]
                print("Adding flow entry:\t{} -> {} in switch {}".format(packet.dst, out_port, switch_number))
                msg = of.ofp_flow_mod()
                msg.match = of.ofp_match()
                msg.match.dl_src = packet.src
                msg.match.dl_dst = packet.dst
                msg.actions.append(of.ofp_action_output(port=out_port))
                self.connection.send(msg)

                # Send the actual packet out
                self.send_packet(event, out_port)

    def send_packet(self, event, out_port):
        msg = of.ofp_packet_out()
        msg.data = event.ofp
        msg.in_port = event.port
        msg.actions.append(of.ofp_action_output(port=out_port))
        self.connection.send(msg)


class LearningController:
    def __init__(self, transparent):
        core.openflow.addListeners(self)

    def _handle_ConnectionUp(self, event):
        Switch(event.connection)


def launch(transparent=False):
    log.info("Pair-Learning switch running.")
    core.registerNew(LearningController, False)

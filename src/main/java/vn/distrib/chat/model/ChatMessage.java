package vn.distrib.chat.model;

public record ChatMessage(String room, String from, String text, long ts) {}

'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import Layout from '@/components/Layout';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/Card';
import { useAuthStore } from '@/lib/store';
import { supabase } from '@/lib/supabase';
import { MessageSquare, Send, Search, Plus, Tag } from 'lucide-react';
import Button from '@/components/Button';
import Input, { Textarea } from '@/components/Input';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';

export default function MessagesPage() {
  const router = useRouter();
  const { user } = useAuthStore();
  const [messages, setMessages] = useState<any[]>([]);
  const [selectedMessage, setSelectedMessage] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [showCompose, setShowCompose] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const loadMessages = useCallback(async () => {
    if (!user) return;

    try {
      const { data: receivedMessages } = await supabase
        .from('message_recipients')
        .select('*, messages(*, profiles(full_name, avatar_url))')
        .eq('recipient_id', user.id)
        .order('created_at', { ascending: false });

      const { data: sentMessages } = await supabase
        .from('messages')
        .select('*, profiles(full_name, avatar_url)')
        .eq('sender_id', user.id)
        .order('created_at', { ascending: false });

      const allMessages = [
        ...(receivedMessages?.map(rm => ({
          ...rm.messages,
          is_received: true,
          is_read: rm.is_read,
        })) || []),
        ...(sentMessages?.map(sm => ({
          ...sm,
          is_received: false,
          is_read: true,
        })) || []),
      ].sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime());

      setMessages(allMessages);
    } catch (error) {
      console.error('Error loading messages:', error);
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    if (!user) {
      router.push('/login');
      return;
    }

    loadMessages();
  }, [user, router, loadMessages]);

  const filteredMessages = messages.filter(msg =>
    msg.subject.toLowerCase().includes(searchQuery.toLowerCase()) ||
    msg.content.toLowerCase().includes(searchQuery.toLowerCase())
  );

  if (!user) return null;

  return (
    <Layout>
      <div className="mb-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">Messagerie</h1>
            <p className="text-gray-600">Communiquez avec les enseignants et étudiants</p>
          </div>
          <Button onClick={() => setShowCompose(true)}>
            <Plus size={18} className="mr-2" />
            Nouveau message
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1">
          <Card>
            <CardHeader>
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                <input
                  type="text"
                  placeholder="Rechercher..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </CardHeader>
            <CardContent className="p-0 max-h-[600px] overflow-y-auto">
              {loading ? (
                <div className="flex justify-center items-center h-32">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                </div>
              ) : filteredMessages.length === 0 ? (
                <div className="text-center py-12">
                  <MessageSquare size={48} className="mx-auto text-gray-400 mb-4" />
                  <p className="text-gray-600">Aucun message</p>
                </div>
              ) : (
                filteredMessages.map((message) => (
                  <div
                    key={message.id}
                    onClick={() => setSelectedMessage(message)}
                    className={`p-4 border-b border-gray-200 cursor-pointer hover:bg-gray-50 transition-colors ${
                      selectedMessage?.id === message.id ? 'bg-blue-50' : ''
                    } ${!message.is_read && message.is_received ? 'bg-blue-50/50' : ''}`}
                  >
                    <div className="flex items-start space-x-3">
                      {message.profiles?.avatar_url ? (
                        <img
                          src={message.profiles.avatar_url}
                          alt={message.profiles.full_name}
                          className="h-10 w-10 rounded-full"
                        />
                      ) : (
                        <div className="h-10 w-10 rounded-full bg-blue-600 flex items-center justify-center text-white font-medium">
                          {message.profiles?.full_name?.charAt(0).toUpperCase()}
                        </div>
                      )}
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center justify-between mb-1">
                          <p className="font-medium text-gray-900 truncate">
                            {message.is_received ? message.profiles?.full_name : 'Moi'}
                          </p>
                          {!message.is_read && message.is_received && (
                            <span className="w-2 h-2 bg-blue-600 rounded-full"></span>
                          )}
                        </div>
                        <p className="text-sm font-medium text-gray-700 truncate">{message.subject}</p>
                        <p className="text-xs text-gray-500 truncate">{message.content}</p>
                        <p className="text-xs text-gray-400 mt-1">
                          {format(new Date(message.created_at), 'PPp', { locale: fr })}
                        </p>
                        {message.tags && message.tags.length > 0 && (
                          <div className="flex flex-wrap gap-1 mt-2">
                            {message.tags.map((tag: string) => (
                              <span key={tag} className="text-xs px-2 py-0.5 bg-gray-100 text-gray-600 rounded">
                                {tag}
                              </span>
                            ))}
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                ))
              )}
            </CardContent>
          </Card>
        </div>

        <div className="lg:col-span-2">
          {showCompose ? (
            <Card>
              <CardHeader>
                <CardTitle>Nouveau message</CardTitle>
              </CardHeader>
              <CardContent>
                <form className="space-y-4">
                  <Input label="Destinataire" placeholder="Rechercher un utilisateur..." />
                  <Input label="Sujet" placeholder="Objet du message" />
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Tags (optionnel)
                    </label>
                    <div className="flex flex-wrap gap-2 mb-2">
                      {['#urgent', '#annonce', '#projet', '#question'].map(tag => (
                        <button
                          key={tag}
                          type="button"
                          className="px-3 py-1 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-full transition-colors"
                        >
                          {tag}
                        </button>
                      ))}
                    </div>
                  </div>
                  <Textarea
                    label="Message"
                    placeholder="Écrivez votre message..."
                    rows={8}
                  />
                  <div className="flex space-x-3">
                    <Button type="submit">
                      <Send size={18} className="mr-2" />
                      Envoyer
                    </Button>
                    <Button type="button" variant="secondary" onClick={() => setShowCompose(false)}>
                      Annuler
                    </Button>
                  </div>
                </form>
              </CardContent>
            </Card>
          ) : selectedMessage ? (
            <Card>
              <CardHeader>
                <div className="flex items-center space-x-3">
                  {selectedMessage.profiles?.avatar_url ? (
                    <img
                      src={selectedMessage.profiles.avatar_url}
                      alt={selectedMessage.profiles.full_name}
                      className="h-12 w-12 rounded-full"
                    />
                  ) : (
                    <div className="h-12 w-12 rounded-full bg-blue-600 flex items-center justify-center text-white font-medium text-lg">
                      {selectedMessage.profiles?.full_name?.charAt(0).toUpperCase()}
                    </div>
                  )}
                  <div>
                    <p className="font-semibold text-gray-900">
                      {selectedMessage.is_received ? selectedMessage.profiles?.full_name : 'Moi'}
                    </p>
                    <p className="text-sm text-gray-500">
                      {format(new Date(selectedMessage.created_at), 'PPPp', { locale: fr })}
                    </p>
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                <h2 className="text-xl font-semibold text-gray-900 mb-4">
                  {selectedMessage.subject}
                </h2>
                {selectedMessage.tags && selectedMessage.tags.length > 0 && (
                  <div className="flex flex-wrap gap-2 mb-4">
                    {selectedMessage.tags.map((tag: string) => (
                      <span key={tag} className="px-3 py-1 bg-blue-100 text-blue-700 rounded-full text-sm">
                        {tag}
                      </span>
                    ))}
                  </div>
                )}
                <div className="prose max-w-none">
                  <p className="text-gray-700 whitespace-pre-wrap">{selectedMessage.content}</p>
                </div>
                {selectedMessage.is_received && (
                  <div className="mt-6 pt-6 border-t border-gray-200">
                    <Button variant="secondary" onClick={() => setShowCompose(true)}>
                      Répondre
                    </Button>
                  </div>
                )}
              </CardContent>
            </Card>
          ) : (
            <Card>
              <CardContent className="text-center py-24">
                <MessageSquare size={64} className="mx-auto text-gray-400 mb-4" />
                <p className="text-gray-600 text-lg">Sélectionnez un message pour le lire</p>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </Layout>
  );
}

'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import Layout from '@/components/Layout';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/Card';
import { useAuthStore, useNotificationStore } from '@/lib/store';
import { supabase, Notification } from '@/lib/supabase';
import { Bell, CheckCircle, AlertCircle, MessageSquare, FileText, Megaphone, Check } from 'lucide-react';
import Button from '@/components/Button';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import Link from 'next/link';

export default function NotificationsPage() {
  const router = useRouter();
  const { user } = useAuthStore();
  const { setUnreadCount } = useNotificationStore();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<'all' | 'unread'>('all');

  const loadNotifications = useCallback(async () => {
    if (!user) return;

    try {
      const { data, error } = await supabase
        .from('notifications')
        .select('*')
        .eq('user_id', user.id)
        .order('created_at', { ascending: false });

      if (error) throw error;

      setNotifications(data || []);
      setUnreadCount(data?.filter(n => !n.is_read).length || 0);
    } catch (error) {
      console.error('Error loading notifications:', error);
    } finally {
      setLoading(false);
    }
  }, [user, setUnreadCount]);

  useEffect(() => {
    if (!user) {
      router.push('/login');
      return;
    }

    loadNotifications();
  }, [user, router, loadNotifications]);

  const markAsRead = async (notificationId: string) => {
    try {
      const { error } = await supabase
        .from('notifications')
        .update({ is_read: true })
        .eq('id', notificationId);

      if (error) throw error;

      setNotifications(notifications.map(n =>
        n.id === notificationId ? { ...n, is_read: true } : n
      ));

      setUnreadCount(Math.max(0, notifications.filter(n => !n.is_read).length - 1));
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const markAllAsRead = async () => {
    try {
      const unreadIds = notifications.filter(n => !n.is_read).map(n => n.id);

      const { error } = await supabase
        .from('notifications')
        .update({ is_read: true })
        .in('id', unreadIds);

      if (error) throw error;

      setNotifications(notifications.map(n => ({ ...n, is_read: true })));
      setUnreadCount(0);
    } catch (error) {
      console.error('Error marking all as read:', error);
    }
  };

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'assignment_created':
        return <FileText className="text-blue-600" size={24} />;
      case 'assignment_graded':
        return <CheckCircle className="text-green-600" size={24} />;
      case 'new_message':
        return <MessageSquare className="text-purple-600" size={24} />;
      case 'deadline_approaching':
        return <AlertCircle className="text-orange-600" size={24} />;
      case 'announcement':
        return <Megaphone className="text-red-600" size={24} />;
      default:
        return <Bell className="text-gray-600" size={24} />;
    }
  };

  const filteredNotifications = notifications.filter(n =>
    filter === 'all' || (filter === 'unread' && !n.is_read)
  );

  if (!user) return null;

  return (
    <Layout>
      <div className="mb-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">Notifications</h1>
            <p className="text-gray-600">
              {notifications.filter(n => !n.is_read).length} notification(s) non lue(s)
            </p>
          </div>
          {notifications.some(n => !n.is_read) && (
            <Button onClick={markAllAsRead}>
              <Check size={18} className="mr-2" />
              Tout marquer comme lu
            </Button>
          )}
        </div>

        <div className="flex space-x-2 mb-6">
          <button
            onClick={() => setFilter('all')}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              filter === 'all'
                ? 'bg-blue-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Toutes
          </button>
          <button
            onClick={() => setFilter('unread')}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              filter === 'unread'
                ? 'bg-blue-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Non lues
          </button>
        </div>
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      ) : filteredNotifications.length === 0 ? (
        <Card>
          <CardContent className="text-center py-12">
            <Bell size={48} className="mx-auto text-gray-400 mb-4" />
            <p className="text-gray-600">Aucune notification</p>
          </CardContent>
        </Card>
      ) : (
        <div className="space-y-3">
          {filteredNotifications.map((notification) => (
            <Card
              key={notification.id}
              className={`transition-all hover:shadow-md ${
                !notification.is_read ? 'bg-blue-50 border-blue-200' : ''
              }`}
            >
              <CardContent>
                <div className="flex items-start space-x-4">
                  <div className="flex-shrink-0 mt-1">
                    {getNotificationIcon(notification.type)}
                  </div>

                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between mb-2">
                      <div className="flex-1">
                        <h3 className="font-semibold text-gray-900 mb-1">
                          {notification.title}
                        </h3>
                        <p className="text-gray-700">{notification.message}</p>
                      </div>
                      {!notification.is_read && (
                        <span className="flex-shrink-0 w-3 h-3 bg-blue-600 rounded-full ml-3 mt-1"></span>
                      )}
                    </div>

                    <div className="flex items-center justify-between">
                      <p className="text-sm text-gray-500">
                        {format(new Date(notification.created_at), 'PPp', { locale: fr })}
                      </p>

                      <div className="flex space-x-2">
                        {notification.link && (
                          <Link href={notification.link}>
                            <Button size="sm" variant="secondary">
                              Voir
                            </Button>
                          </Link>
                        )}
                        {!notification.is_read && (
                          <Button
                            size="sm"
                            variant="ghost"
                            onClick={() => markAsRead(notification.id)}
                          >
                            Marquer comme lu
                          </Button>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </Layout>
  );
}

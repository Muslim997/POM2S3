'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Layout from '@/components/Layout';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/Card';
import { useAuthStore } from '@/lib/store';
import Input from '@/components/Input';
import Button from '@/components/Button';
import { Textarea } from '@/components/Input';

export default function NewAssignmentPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { user } = useAuthStore();
  const [loading, setLoading] = useState(false);
  const [courses, setCourses] = useState<any[]>([]);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    subject_id: searchParams.get('course') || '',
    due_date: '',
    max_points: 20,
  });

  useEffect(() => {
    if (!user || (user.role !== 'teacher' && user.role !== 'admin')) {
      router.push('/dashboard');
      return;
    }
    loadCourses();
  }, [user, router]);

  const loadCourses = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('/api/courses', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setCourses(data);
      }
    } catch (error) {
      console.error('Error loading courses:', error);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const token = localStorage.getItem('token');
      const response = await fetch('/api/assignments', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        router.push('/assignments');
      } else {
        alert('Erreur lors de la création du devoir');
      }
    } catch (error) {
      console.error('Error creating assignment:', error);
      alert('Erreur lors de la création du devoir');
    } finally {
      setLoading(false);
    }
  };

  if (!user || (user.role !== 'teacher' && user.role !== 'admin')) {
    return null;
  }

  return (
    <Layout>
      <div className="max-w-2xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Créer un nouveau devoir</h1>
          <p className="text-gray-600">Assignez un nouveau travail à vos étudiants</p>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Détails du devoir</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              <Input
                label="Titre du devoir"
                placeholder="Ex: Projet Web Avancé - Phase 1"
                value={formData.title}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                required
              />

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Cours associé
                </label>
                <select
                  value={formData.subject_id}
                  onChange={(e) => setFormData({ ...formData, subject_id: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  required
                >
                  <option value="">Sélectionnez un cours</option>
                  {courses.map((course) => (
                    <option key={course.id} value={course.id}>
                      {course.code} - {course.title}
                    </option>
                  ))}
                </select>
              </div>

              <Textarea
                label="Description et consignes"
                placeholder="Décrivez les objectifs, les exigences et les critères d'évaluation..."
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                rows={6}
                required
              />

              <Input
                type="datetime-local"
                label="Date limite"
                value={formData.due_date}
                onChange={(e) => setFormData({ ...formData, due_date: e.target.value })}
                required
              />

              <Input
                type="number"
                label="Points maximum"
                min="1"
                max="100"
                value={formData.max_points}
                onChange={(e) => setFormData({ ...formData, max_points: parseInt(e.target.value) })}
                required
              />

              <div className="flex space-x-3">
                <Button type="submit" disabled={loading}>
                  {loading ? 'Création...' : 'Créer le devoir'}
                </Button>
                <Button type="button" variant="secondary" onClick={() => router.back()}>
                  Annuler
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </Layout>
  );
}